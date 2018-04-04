package spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import spring.view.CategoryType;
import spring.view.CategoryValue;
import spring.view.NotTVUser;
import spring.view.User;
import spring.view.Playlist;
import spring.view.Video;
import spring.view.VideoData;

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
	    rs.getInt("id"),
	    rs.getString("name"),
	    jdbcTemplate.query(
		"Select id, name From category_value Where categorytypeid=?;",
		(cvRs, cvRowNum) -> new CategoryValue(cvRs.getInt("id"), cvRs.getString("name")),
		rs.getInt("id")
	    )
	));
    }
    
    // Returns a new unique id for the category_type table
    @GetMapping("/category-type-id")
    @ResponseBody
    public int getNextCategoryTypeId() {
        int id = jdbcTemplate.query("Select nextval('category_type_id_seq');", (rs) -> {rs.next(); return rs.getInt(1);});
        log.info("Next categoryType id: " + id);
        return id;
    }
    
    // Returns a new unique id for the category_value table
    @GetMapping("/category-value-id")
    @ResponseBody
    public int getNextCategoryValueId() {
        int id = jdbcTemplate.query("Select nextval('category_value_id_seq');", (rs) -> {rs.next(); return rs.getInt(1);});
        log.info("Next categoryValue id: " + id);
        return id;
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
    public boolean checkForSubscription(@RequestParam(value="subscriber", required=true) int userID1,
    @RequestParam(value="author", required=true) int userID2 ) {
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
    
    @GetMapping("/subscriptions")
    @ResponseBody
    public List<User> getSubscribedUsers(@RequestParam(value="loggedInUserID", required=true) int loggedInUserID) {
        log.info("get users that you are already subscribed to for the AccountInfo page");

        String query = "Select id, username, profilepictureurl, userprofileurl From nottv_user WHERE id IN (Select authorId From subscribe Where subscriberId = ?)";
        log.info(query);
        
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, loggedInUserID);
                return ps;
            }
        };
        
        // put the data into a view object.
        return jdbcTemplate.query(psc, (rs, row) -> new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("userprofileurl"),
            rs.getString("profilepictureurl")
        )
        ); 
    }
    
    @GetMapping("/recentVideos")
    @ResponseBody
    public List<Video> getRecentlyUploadedVideos(@RequestParam(value="userid", required=false) int userid) {
        log.info("get videos one specific user recently uploaded... we need it for UserProfile page");

        String query = "Select v.id As vid, title, downloadurl, u.id As uid, username From video v INNER JOIN nottv_user u ON v.userid = u.id WHERE u.id = ? LIMIT 10";
        log.info(query);
        
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, userid);
                return ps;
            }
        };
        
        // put the data into a view object.
        return jdbcTemplate.query(psc, (rs, row) -> new Video(
            rs.getInt("vid"),
            rs.getString("title"), 
            new NotTVUser(
                rs.getInt("uid"),
                rs.getString("username")
            )
        )); 
    }
    
    
    @GetMapping("/libraryVideos")
    @ResponseBody
    public List<Video> getLibraryVideos() {
        // Video(title, thumbnail_url, download_url)
        log.info("get videos that are in-library");

        // outer query: return all video info where videoid is in [inner query result]
        // inner query: return all videoids from category_value_type joint table where categoryvalueid = 10, (AKA category value = IN LIBRARY)
        
        String query = "Select v.id As vid, title, downloadurl, u.id As uid, username From video v INNER JOIN nottv_user u ON v.userid = u.id WHERE v.id IN (SELECT videoid from video_category_value_join WHERE categoryvalueid = 10)";
        log.info(query);
        

        // put the data into a view object.
        return jdbcTemplate.query(query, (rs, row) -> new Video(
            rs.getInt("vid"),
            rs.getString("title"), 
            new NotTVUser(
                rs.getInt("uid"),
                rs.getString("username")
            )
        )); 
    }

    @GetMapping("/videos")
    @ResponseBody
    public List<Video> getVideos(@RequestParam(value="categories[]", required=false) int[] categories,
    @RequestParam(value="searchTarget", required=false) String searchTarget, 
    @RequestParam(value="searchText", required=false) String searchText,
    @RequestParam(value="searchOrder", required=true) String searchOrder) {
	// Video(title, thumbnail_url, download_url)
	log.info("videos");

	// Make the query. It looks terrible, but it should be pretty efficient since
	// the Intersect tables will be small, and the filters on the id can be pushed up before the joins.
	// Also, the intersects can be used to filter subsequent results
        StringBuilder queryBuilder = new StringBuilder("Select v.id As vid, title, downloadurl, u.id As uid, username From video v INNER JOIN nottv_user u ON v.userid = u.id ");
        ArrayList<Object> queryParams = new ArrayList<>();
        // filter video id to exist in the intersection of the categories specified.
	if(categories != null && categories.length > 0) { // Only filter results if categories are specified.
            queryBuilder.append("Where v.id in (");
	    for(int i=0; i<categories.length;i++) {
		if(i != 0) // No intersect on first one.
		    queryBuilder.append("Intersect ");
    		queryBuilder.append("Select videoid ");
    		queryBuilder.append("From video_category_value_join As vcvj Join category_value As cv On cv.id=vcvj.categoryvalueid ");
    		queryBuilder.append("Where cv.id = ?");
    		queryParams.add(categories[i]);
	    }
	    queryBuilder.append(')');
	}
        if(searchText != null && searchText.length() > 0){  //If search text is present, add appropriate 'LIKE' condition to the query
            switch(searchTarget){
            case "title":
                queryBuilder.append(" And title ILIKE ?");
                break;
            case "uploader":
                queryBuilder.append(" And username ILIKE ?");
                break;
            default:
                log.error("Invalid searchTarget parameter: "+searchTarget);
                break;
            }
            queryParams.add("%"+searchText+"%");
        }
        switch(searchOrder){    // sort according to searchOrder
        case "time asc":
            queryBuilder.append(" ORDER BY vid ASC");
            break;
        case "time desc":
            queryBuilder.append(" ORDER BY vid DESC");
            break;
        default:
            log.error("Invalid searchOrder parameter: "+searchOrder);
            break;
        }
	queryBuilder.append(';');
	String query = queryBuilder.toString();
	log.info(query);
	for(Object param: queryParams.toArray())
	    log.info(param.toString());

        // put the data into a view object.
	return jdbcTemplate.query(query, (rs, row) -> new Video(
            rs.getInt("vid"),
            rs.getString("title"), 
            new NotTVUser(
                rs.getInt("uid"),
                rs.getString("username")
            )
        ),queryParams.toArray()); 
    }

    @GetMapping("/video-data")
    @ResponseBody
    public VideoData getVideoData(@RequestParam(value="videoId", required=true) int videoId) {
        log.info("video data");
        StringBuilder queryBuilder = new StringBuilder("Select video.id, title, description, userid, username ");
        queryBuilder.append("From video Inner Join nottv_user On nottv_user.id = Video.userid ");
        queryBuilder.append("Where video.id = ?;");
        String query = queryBuilder.toString();
        log.info(query +" ,"+videoId);

        return jdbcTemplate.query(query, new Object[] {videoId}, (rs) -> {
            rs.next();
            int id = rs.getInt("id");
            String title = rs.getString("title");
            String desc = rs.getString("description");
            int userId = rs.getInt("userid");
            String userName = rs.getString("username");

            boolean subbed = jdbcTemplate.query("Select 1 From subscribe Where authorid = ? And subscriberid = ?;",
            new Object[] {userId, 1} , (rs2) -> {return rs2.next();});

            return new VideoData(id, title, desc, userId, userName, subbed);
        });
    }

    @GetMapping("/public-ip")
    @ResponseBody
    public String getPublicIp(HttpServletRequest request) {
	return request.getRemoteAddr();
    }

    @GetMapping("/criteria")
    @ResponseBody
    public List<String> getCriteria() {
        return Arrays.asList(
            "All Videos submitted must be original content, remixed content is permitted if it does not infringe any copyrights.",
            "Traditional music videos and live performance videos will be accepted.",
            "Videos may be submitted by any member of the team that created them.",
            "Content contributors must be able to demonstrate the ownership and right to distribute the video on the internet to a worldwide audience.",
            "There is no minimum or maxiumum length for the videos.",
            "videos must be in high definition (minimum 720p, preferred 1080p).",
            "Multiple submissions are allowed.",
            "For each video you submit, you grant notTV a non-exclusive, worldwide electronic distribution license to stream the video on www.not.tv or any of the websites owned by notTV, anywhere in the world, electronically, in perpituity.",
            "All money after expenses, generated by the operations of notTV, is distributed to the member-owners of notTV.",
            "You must be a notTV member for your content to be eligible for commercial revenue generation."
        );
    }
}
