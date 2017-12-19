package example;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.CredentialExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.ibm.websphere.security.WSSecurityException;
import com.ibm.websphere.security.auth.CredentialDestroyedException;
import com.ibm.websphere.security.auth.WSSubject;
import com.ibm.websphere.security.cred.WSCredential;
import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtToken;

// http://localhost:9080/getJwtToken

@ApplicationPath("/")
@Path("/")
public class TokenGen extends Application {
	private final static String GROUP_PREFIX="group:";
	   private final static String USER_PREFIX="user:";
	
	@Context
	SecurityContext secCon;
	
	@Context                                               
	HttpServletRequest request;
	
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String getJWTString2(){
		return getTokenBean().getToken();
	}
	
	@GET
	@Path("/text")
	@Produces(MediaType.TEXT_PLAIN)
	public String getJWTString(){
		return getTokenBean().getToken();
	}
	
	@GET
	@Path("/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Token getJWT() {
		return getTokenBean();
    }
	
	Token getTokenBean(){
		Subject subject = null;
		try {
			subject = WSSubject.getRunAsSubject();
		} catch (WSSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		WSCredential wsCred = getWSCredential( subject);
		
		ArrayList<String> groupIds = null;
		try {
			groupIds = wsCred.getGroupIds();
		} catch (CredentialExpiredException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (CredentialDestroyedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
        ArrayList <String> groups = new ArrayList <String>();
        Iterator <String> it = groupIds.listIterator();
        while (it.hasNext()){
            String origGroup = it.next();
            if (origGroup != null && origGroup.startsWith(GROUP_PREFIX)) {
                int groupIndex = origGroup.indexOf("/");
                if (groupIndex > 0) {
                    origGroup = origGroup.substring(groupIndex + 1);
                }
            }
            groups.add(origGroup);
        }
        
      
		
		Principal p =  secCon.getUserPrincipal();
		String user =   p != null ?  p.getName() : "null";
		JwtBuilder builder = null;
		String jwtToken = null;
		long exptime = 0;
		try {
			builder = com.ibm.websphere.security.jwt.JwtBuilder.create();
			
			
			builder.subject(user);
			builder.claim("upn", user);
			builder.claim("groups", groups);
			builder.claim("iss", request.getRequestURL().toString());
					
			JwtToken theToken = builder.buildJwt();	
            exptime = theToken.getClaims().getExpiration();
			jwtToken = theToken.compact();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		Token t = new Token();
		t.setToken( jwtToken);
		t.setExpires(Long.toString(exptime));
		return t;
		
		
	}
	
	public WSCredential getWSCredential(Subject subject) {
        WSCredential wsCredential = null;
        Set<WSCredential> wsCredentials = subject.getPublicCredentials(WSCredential.class);
        Iterator<WSCredential> wsCredentialsIterator = wsCredentials.iterator();
        if (wsCredentialsIterator.hasNext()) {
            wsCredential = wsCredentialsIterator.next();
        }
        return wsCredential;
    }
	

}
