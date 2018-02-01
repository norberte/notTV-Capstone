package spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import spring.model.VideoData;
import spring.view.CategoryType;
import spring.view.CategoryValue;
import spring.view.Video;
import spring.view.Playlist;

@CrossOrigin
@RestController
@RequestMapping("/info")
public class InfoController {
    private static final Logger log = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/categories")
    @ResponseBody
    public List<CategoryType> getCategories() {
	log.info("categories");
	// CategoryValue(id, name)
	// CategoryType(name, CategoryValue[] values)
	return jdbcTemplate.query("Select * From category_type;", (rs, rowNum) -> new CategoryType(
	    rs.getString("name"),
	    jdbcTemplate.query(
		"Select id, name From category_value Where categorytypeid=?;",
		(cvRs, cvRowNum) -> new CategoryValue(cvRs.getInt("id"), cvRs.getString("name")),
		rs.getInt("id")
	    )
	));
    }
    
    // gets playlists owned by a user. 
    // takes in a userID parameter
    @GetMapping("/playlists")
    @ResponseBody
    public List<Playlist> getPlaylists(@RequestParam(value="userid[]", required=true) int[] userid) {
    // Video(title, thumbnail_url, download_url)
    log.info("playlists owned by a user");

    // Make the query.
    String query = "Select title,thumbnailurl, downloadurl From Playlist Where owner = ?";
    log.info(query);
    
    PreparedStatementCreator psc = new PreparedStatementCreator() {
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userid[0]); // json of length 1 is sent, with only one userid inside the json object
            return ps;
        }
    };
    
    return jdbcTemplate.query(psc, (rs, row) -> new Playlist(
        rs.getString("title"), 
        rs.getString("thumbnailurl"), // TODO: make sure this is correct.
        rs.getString("downloadurl") // TODO: implement the download for a whole playlist
        )
    ); 
    }

    @GetMapping("/getUserID")
    @ResponseBody
    public List<Integer> getUserID(@RequestParam(value="username[]", required=true) String[] username) {
    // Video(title, thumbnail_url, download_url)
    log.info("return userID given username");

    // Make the query.
    String query = "Select id From nottv_user Where username = ?;";
    log.info(query);
    
    PreparedStatementCreator psc = new PreparedStatementCreator() {
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username[0]); // json of length 1 is sent, with only one username inside the json object
            return ps;
        }
    };
    
    return jdbcTemplate.query(psc, (rs, row) -> new Integer(rs.getInt("id"))); 
    }
    
    @GetMapping("/checkSubscribed")
    @ResponseBody
    public boolean checkForSubscription(@RequestParam(value="userID1", required=true) int userID1,
                        @RequestParam(value="userID2", required=true) int userID2 ) {
    log.info("Given userID1 and userID2, check if userid1 is subscripted to userId2");

    // Make the query.
    String query = "Select authorId From subscribe Where subscriberId = ? AND authorId = ?;";
    log.info(query);
    
    PreparedStatementCreator psc = new PreparedStatementCreator() {
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userID1);  // user1, the loggedIn user, is the subscriber
            ps.setInt(2, userID2);  // user2, the user who's profile is being checked, is the author
            return ps;
        }
    };
    
        List<Integer> result = jdbcTemplate.query(psc, (rs, row) -> new Integer(rs.getInt("authorId")));
        log.info("Query result: " + Arrays.toString(result.toArray()));
        if(result.size() > 0) {
            log.info("user is subscibed");
            return true;
        } else {
            log.info("user is unsubscibed");
            return false;
        }
    }
    
    
    
    @GetMapping("/recentVideos")
    @ResponseBody
    public List<Video> getRecentVideos(@RequestParam(value="userid[]", required=true) int[] userid) {
    // Video(title, thumbnail_url, download_url)
    log.info("recent videos");

    // Make the query.
    String query = "Select title, downloadurl, thumbnailurl From Video Where userid = ? Limit 10";
    log.info(query);
    
    PreparedStatementCreator psc = new PreparedStatementCreator() {
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userid[0]); // json of length 1 is sent, with only one userid inside the json object
            return ps;
        }
    };
    
    return jdbcTemplate.query(psc, (rs, row) -> new Video(
        rs.getString("title"), 
        rs.getString("thumbnailurl"), //TODO: make sure this is correct.
        "/process/download?torrentName="+rs.getString("downloadurl"))
    ); 
    }
    
    
    
    @GetMapping("/videos")
    @ResponseBody
    public List<Video> getVideos(@RequestParam(value="categories[]", required=false) int[] categories) {
	// Video(title, thumbnail_url, download_url)
	log.info("videos");

	// Make the query. It looks terrible, but it should be pretty efficient since
	// the Intersect tables will be small, and the filters on the id can be pushed up before the joins.
	// Also, the intersects can be used to filter subsequent results
	StringBuilder queryBuilder = new StringBuilder("Select title, downloadurl, thumbnailurl From Video ");
	
	if(categories != null && categories.length > 0) { // Only filter results if categories are specified.
	    queryBuilder.append("Where id in (");
	    for(int i=0; i<categories.length;i++) {
		if(i != 0) // No intersect on first one.
		    queryBuilder.append("Intersect ");
		queryBuilder.append("Select videoid ");
		queryBuilder.append("From video_category_value_join As vcvj Join category_value As cv On cv.id=vcvj.categoryvalueid ");
		queryBuilder.append("Where cv.id = ");
		queryBuilder.append(categories[i]);
	    }
	    queryBuilder.append(')');
	}
	queryBuilder.append(';');
	String query = queryBuilder.toString();
	log.info(query);
	
	return jdbcTemplate.query(query, (rs, row) -> new Video(
	    rs.getString("title"), 
	    rs.getString("thumbnailurl"), //TODO: make sure this is correct.
	    "/process/download?torrentName="+rs.getString("downloadurl"))
	); 
    }
    
    
    @GetMapping("/video-data")
    @ResponseBody
    public List<VideoData> getVideoData(@RequestParam(value="videoId", required=true) int videoId) {
    log.info("video data");
    StringBuilder queryBuilder = new StringBuilder("Select title, description, userid, username ");
    queryBuilder.append("From Video Inner Join nottv_user On nottv_user.id = Video.userid ");
    queryBuilder.append("Where video.id = ");
    queryBuilder.append(videoId);
    queryBuilder.append(';');
    String query = queryBuilder.toString();
    log.info(query);
    
    return jdbcTemplate.query(query, (rs, row) -> new VideoData(
        rs.getString("title"), 
        rs.getString("description"),
        rs.getInt("userid"),
        rs.getString("username"))
    ); 
    }
}
