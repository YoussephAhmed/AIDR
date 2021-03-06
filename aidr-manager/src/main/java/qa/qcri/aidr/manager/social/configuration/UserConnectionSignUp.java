package qa.qcri.aidr.manager.social.configuration;

import java.sql.Timestamp;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth1Connection;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.stereotype.Component;

import qa.qcri.aidr.manager.persistence.entities.UserAccount;
import qa.qcri.aidr.manager.persistence.entities.UserConnection;
import qa.qcri.aidr.manager.service.UserConnectionService;
import qa.qcri.aidr.manager.service.UserService;
import qa.qcri.aidr.manager.util.Constants;

@Component("userConnectionSignUp")
public class UserConnectionSignUp implements ConnectionSignUp {

	@Inject
	private UserConnectionService userConnectionService;
	
	@Resource(name="userService")
	private UserService userService;
	
	@Override
    public String execute(Connection<?> connection) {
		if(connection instanceof OAuth1Connection){
			OAuth1Connection<?> oauthConnection = (OAuth1Connection<?>) connection;
			ConnectionData data = oauthConnection.createData();
	        UserProfile profile = oauthConnection.fetchUserProfile();
	        UserConnection userConnection = new UserConnection();
	        userConnection.setUserId(profile.getUsername());
	        userConnection.setImageUrl(data.getImageUrl());
	        userConnection.setProfileUrl(data.getProfileUrl());
	        userConnection.setAccessToken(data.getAccessToken());
	        userConnection.setSecret(data.getSecret());
	        userConnection.setRefreshToken(data.getRefreshToken());
	        userConnection.setProviderUserId(data.getProviderUserId());
	        /**
	         * This is custom SignUp and for it providerID should be springSecurity
	         */
	        userConnection.setProviderId("springSocialSecurity");
	        userConnection.setDisplayName(data.getDisplayName());
	        userConnection.setRank(1);
	        userConnectionService.register(userConnection);
	        
	        if(userService.fetchByUserName(data.getProviderId() + Constants.USER_NAME_SPLITTER + profile.getUsername()) == null){
	        	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		        UserAccount user = new UserAccount();
		        user.setApiKey(UUID.randomUUID().toString());
		        user.setCreatedAt(currentTimestamp);
		        user.setUpdatedAt(currentTimestamp);
		        user.setProvider(data.getProviderId());
		        user.setUserName(data.getProviderId() + Constants.USER_NAME_SPLITTER + profile.getUsername());
		        userService.save(user);
	        }
	        return profile.getUsername();
	        
		}else if (connection instanceof OAuth2Connection) {
			OAuth2Connection<?> oauthConnection = (OAuth2Connection<?>) connection;
			ConnectionData data = oauthConnection.createData();
			UserProfile profile = oauthConnection.fetchUserProfile();
			
			UserConnection userConnection = new UserConnection();
			userConnection.setUserId(profile.getEmail());
			userConnection.setImageUrl(data.getImageUrl());
			userConnection.setProfileUrl(data.getProfileUrl());
			userConnection.setAccessToken(data.getAccessToken());
			userConnection.setSecret(data.getSecret());
			userConnection.setRefreshToken(data.getRefreshToken());
			userConnection.setProviderUserId(data.getProviderUserId());
			userConnection.setProviderId("springSocialSecurity");
			userConnection.setDisplayName(data.getDisplayName());
			userConnection.setRank(1);
			userConnectionService.register(userConnection);
			
			if(userService.fetchByUserName(data.getProviderId() + Constants.USER_NAME_SPLITTER + profile.getEmail()) == null){
				Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
				UserAccount user = new UserAccount();
				user.setApiKey(UUID.randomUUID().toString());
				user.setCreatedAt(currentTimestamp);
				user.setUpdatedAt(currentTimestamp);
				user.setProvider(data.getProviderId());
				user.setUserName(data.getProviderId() + Constants.USER_NAME_SPLITTER + profile.getEmail());
				user.setEmail(profile.getEmail());
				userService.save(user);
			}
			
			return profile.getEmail();
		}
		
		return connection.fetchUserProfile().getUsername();
    }

}
