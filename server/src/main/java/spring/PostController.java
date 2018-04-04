package spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import spring.model.User;
import spring.storage.StorageService;
import spring.view.AccountForm;
import spring.view.VideoForm;

@CrossOrigin
@RestController
@RequestMapping("/upload")
public class PostController {
    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    @Qualifier("ImageStorage")
    private StorageService thumbnailStorage;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping(value="/add-video", consumes={"multipart/form-data"})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public int processVideoInfo(
        @RequestPart("videoForm") @Valid VideoForm videoForm,
        @RequestPart("thumbnail") @Valid @NotNull @NotBlank MultipartFile thumbnail
    ) {
        log.info("Adding video {}...", videoForm.getTitle());
        // insert statement
        final String SQL = "INSERT INTO video (title, description, version, license, userID) VALUES(?,?,?,?,?)";
        final String CAT_SQL = "Insert Into video_category_value_join (videoid, categoryvalueid) Values(?,?);";
        try (
            Connection connection = jdbc.getDataSource().getConnection();
            PreparedStatement ps = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement catPs = connection.prepareStatement(CAT_SQL);
        ) {
            ps.setString(1, videoForm.getTitle());
            ps.setString(2, videoForm.getDescription());
            ps.setInt(3, videoForm.getVersion());
            ps.setString(4, videoForm.getLicense());
            ps.setInt(5, videoForm.getUserid());
            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0)
                throw new SQLException("Failed to create video, no rows affected.");

            int id;
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next())
                    id = generatedKeys.getInt(1);
                else
                    throw new SQLException("Failed to create video, no ID obtained.");
            }

            // Store thumbnail
            thumbnailStorage.store(String.valueOf(id), thumbnail);

            // add categories
            catPs.setInt(1, id);
            for(int cat : videoForm.getTags()) {
                catPs.setInt(2, cat); // categoryvalueid = cat
                catPs.executeUpdate();
            }
            return id;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1; // no video.
    }

    private int getUserID(String username) {
        // Make the query.
        StringBuilder queryBuilder = new StringBuilder("Select id From nottv_user Where username = ?;");
        String query = queryBuilder.toString();

        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, username); // json of length 1 is sent, with only one username inside the json object
                return ps;
            }
        };

        List<Integer> id = this.jdbc.query(psc, (rs, row) -> new Integer(rs.getInt("id")));
        if(id.size() > 0) {
            return id.get(0);
        } else {
            return -10;
        }
    }

    private int getUserSettingsPK(int user_id) {
        // Make the query.
        StringBuilder queryBuilder = new StringBuilder("Select id From settings Where userId = ?;");
        String query = queryBuilder.toString();

        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, user_id); // json of length 1 is sent, with only one username inside the json object
                return ps;
            }
        };

        List<Integer> id = this.jdbc.query(psc, (rs, row) -> new Integer(rs.getInt("id")));
        if(id.size() > 0) {
            return id.get(0);
        } else {
            return -1;
        }
    }

    @PostMapping("/accountSubmit")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void processAccountInfoChanges(@RequestBody(required=true) AccountForm accountInfo) {
        // update statements
        final String emailUpdate_query = "UPDATE nottv_user SET email = ? WHERE id = ?;";
        final String passwordUpdate_query = "UPDATE nottv_user SET password = ? WHERE id = ?;";
        final String autoDownload_update = "UPDATE settings SET autodownload = ? WHERE id = ?;";

        // insert statement
        final String autoDownload_insert = "INSERT INTO settings(userId, alwaysDownload) VALUES(?,?);";

        String username = accountInfo.getCurrentUsername();
        int user_id = getUserID(username);
        if(user_id == -10) {    // username does not belong to a userid... non-existent account
            return;
        }

        String newPass = accountInfo.getNewPass();
        String confirmedNewPass = accountInfo.getConfirmNewPass();

        // change password, if the 2 passwords match
        if(newPass.equals(confirmedNewPass)) {
            // encrypt password
            String encryptedPassword = encoder.encode(newPass);
            PreparedStatementCreator psc = new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(passwordUpdate_query);
                    ps.setString(1, encryptedPassword);
                    ps.setInt(2, user_id);
                    return ps;
                }
            };
            this.jdbc.update(psc);
        }

        // change e-mail address
        String newEmail = accountInfo.getNewEmail();
        PreparedStatementCreator psc2 = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(emailUpdate_query);
                ps.setString(1, newEmail);
                ps.setInt(2, user_id);
                return ps;
            }
        };
        this.jdbc.update(psc2);

        // change auto-download
        boolean autoDownloading;
        String autoDownload = accountInfo.getAutoDownload(); // take care of this
        if(autoDownload.equals("T")){
            autoDownloading = true;
        } else {
            autoDownloading =  false;
        }

        int settings_id = getUserSettingsPK(user_id);
        if(settings_id == -1) { // if user does not have settings yet, insert into settings table
            PreparedStatementCreator psc = new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(autoDownload_insert);
                    ps.setInt(1, user_id);
                    ps.setBoolean(2, autoDownloading);
                    return ps;
                }
            };

            this.jdbc.update(psc);
        } else {    // if user_id is already present in the settings table, just update auto-download
            PreparedStatementCreator psc = new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(autoDownload_update);
                    ps.setBoolean(1, autoDownloading);
                    ps.setInt(2, settings_id);
                    return ps;
                }
            };
            this.jdbc.update(psc);
        }
    }




    //Receive POST from login.js and check hashed password matches hashed password in DB.
    //returns id of user if login is authorized. NULL if unnauthorized.
    @PostMapping("authenticate-login")
    public User authenticateLogin(@RequestParam(value="email", required=true) String email, @RequestParam(value="password", required=true) String passPOST){
        //Query DB for password
        final String passHash_query = "SELECT id, password FROM nottv_user WHERE email = ?";
        User user = jdbc.query(passHash_query, new Object[] {email}, (rs) -> {
            return new User(rs.getInt("id"), rs.getString("password"));
        });

        // If the given password matches the hashed password.
        if(encoder.matches(passPOST, user.getPassword())) {
            return user;
        }
        return null;
    }
}
