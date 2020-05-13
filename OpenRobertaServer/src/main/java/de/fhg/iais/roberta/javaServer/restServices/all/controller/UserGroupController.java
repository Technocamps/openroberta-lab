package de.fhg.iais.roberta.javaServer.restServices.all.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import de.fhg.iais.roberta.javaServer.provider.OraData;
import de.fhg.iais.roberta.main.MailManagement;
import de.fhg.iais.roberta.persistence.ProcessorStatus;
import de.fhg.iais.roberta.persistence.UserGroupProcessor;
import de.fhg.iais.roberta.persistence.UserProcessor;
import de.fhg.iais.roberta.persistence.bo.User;
import de.fhg.iais.roberta.persistence.bo.UserGroup;
import de.fhg.iais.roberta.persistence.util.DbSession;
import de.fhg.iais.roberta.persistence.util.HttpSessionState;
import de.fhg.iais.roberta.robotCommunication.RobotCommunicator;
import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.util.ServerProperties;
import de.fhg.iais.roberta.util.Statistics;
import de.fhg.iais.roberta.util.Util;
import de.fhg.iais.roberta.util.UtilForREST;

@Path("/userGroup")
public class UserGroupController {

    private static final Logger LOG = LoggerFactory.getLogger(UserGroupController.class);

    private final RobotCommunicator brickCommunicator;
    private final MailManagement mailManagement;

    private final boolean isPublicServer;

    private static String[] statusText = new String[2];
    private static long statusTextTimestamp;

    @Inject
    public UserGroupController(RobotCommunicator brickCommunicator, ServerProperties serverProperties, MailManagement mailManagement) {
        this.brickCommunicator = brickCommunicator;
        this.mailManagement = mailManagement;
        this.isPublicServer = serverProperties.getBooleanProperty("server.public");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getUserGroup")
    public Response getUserGroup(@OraData DbSession dbSession, JSONObject fullRequest) throws Exception {
        String cmd = "getUserGroup";

        JSONObject response = new JSONObject();

        try {
            response.put("cmd", cmd);
        } catch ( JSONException e ) {
            // Can not happen, because the key is neither null, nor is the value numeric and infinite
            e.printStackTrace();
        }

        UserGroupController.LOG.info("command is: " + cmd);

        HttpSessionState httpSessionState = UtilForREST.handleRequestInit(UserGroupController.LOG, fullRequest);

        if ( !httpSessionState.isUserLoggedIn() ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            UtilForREST.addErrorInfo(response, Key.USER_ERROR_NOT_LOGGED_IN);
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        int loggedInUserId = httpSessionState.getUserId();
        String groupName;

        try {
            groupName = fullRequest.getJSONObject("data").getString("groupName");
        } catch ( JSONException e ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            UtilForREST.addErrorInfo(response, Key.COMMAND_INVALID);
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        try {
            if ( !httpSessionState.isUserLoggedIn() ) {
                UserGroupController.LOG.error("Invalid command: " + cmd);
                UtilForREST.addErrorInfo(response, Key.COMMAND_INVALID);
            } else {
                UserProcessor up = new UserProcessor(dbSession, httpSessionState);
                User user = up.getUser(httpSessionState.getUserId());

                UtilForREST.addResultInfo(response, up);

                if ( user != null ) {

                    UserGroupProcessor ugp = new UserGroupProcessor(dbSession, httpSessionState, this.isPublicServer);

                    int id = user.getId();
                    String account = user.getAccount();
                    String userName = user.getUserName();
                    String email = user.getEmail();
                    boolean age = user.isYoungerThen14();
                    response.put("userId", id);
                    response.put("userAccountName", account);
                    response.put("userName", userName);
                    response.put("userEmail", email);
                    response.put("isYoungerThen14", age);
                }
            }
        } catch ( Exception e ) {
            dbSession.rollback();
            String errorTicketId = Util.getErrorTicketId();
            UserGroupController.LOG.error("Exception. Error ticket: " + errorTicketId, e);
            UtilForREST.addErrorInfo(response, Key.SERVER_ERROR).append("parameters", errorTicketId);
        } finally {
            if ( dbSession != null ) {
                dbSession.close();
            }
        }
        return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getUserGroupList")
    public Response getUserGroupListForUser(@OraData DbSession dbSession, JSONObject fullRequest) throws Exception {
        String cmd = "getUserGroup";

        JSONObject response = new JSONObject();

        try {
            response.put("cmd", cmd);
        } catch ( JSONException e ) {
            // Can not happen, because the key is neither null, nor is the value numeric and infinite
            e.printStackTrace();
        }

        UserGroupController.LOG.info("command is: " + cmd);

        HttpSessionState httpSessionState = UtilForREST.handleRequestInit(UserGroupController.LOG, fullRequest);

        if ( !httpSessionState.isUserLoggedIn() ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            UtilForREST.addErrorInfo(response, Key.USER_ERROR_NOT_LOGGED_IN);
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        UserProcessor up = new UserProcessor(dbSession, httpSessionState);
        User groupOwner;

        try {
            groupOwner = up.getUser(httpSessionState.getUserId());
        } catch ( Exception e ) {
            try {
                UtilForREST.addErrorInfo(response, up.getMessage());
            } catch ( JSONException e1 ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        if ( groupOwner == null ) {
            UtilForREST.addErrorInfo(response, Key.SERVER_ERROR);
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        UserGroupProcessor ugp = new UserGroupProcessor(dbSession, httpSessionState, this.isPublicServer);
        List<UserGroup> userGroups = ugp.getGroupsByOwner(groupOwner);

        Statistics.info(cmd, "success", ugp.succeeded());

        try {
            UtilForREST.addResultInfo(response, ugp);
        } catch ( Exception e ) {
            //ignore
        }

        if ( ugp.succeeded() ) {
            JSONArray userGroupInfos = new JSONArray();
            for ( UserGroup userGroup : userGroups ) {
                userGroupInfos.put(userGroup.toListJSON());
            }
            response.put("userGroups", userGroupInfos);
        }

        return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
    }

    /**
     * TODO: Check for isPublic on whether or not a global user can have a group or not
     *
     * @param dbSession
     * @param fullRequest
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/createUserGroup")
    public Response createUserGroup(@OraData DbSession dbSession, JSONObject fullRequest) {
        String cmd = "createUserGroup";
        JSONObject response = new JSONObject();

        try {
            response.put("cmd", cmd);
        } catch ( JSONException e ) {
            // Can not happen, because the key is neither null, nor is the value numeric and infinite
            e.printStackTrace();
        }

        UserGroupController.LOG.info("command is: " + cmd);

        HttpSessionState httpSessionState = UtilForREST.handleRequestInit(UserGroupController.LOG, fullRequest);

        if ( !httpSessionState.isUserLoggedIn() ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            try {
                UtilForREST.addErrorInfo(response, Key.USER_ERROR_NOT_LOGGED_IN);
            } catch ( JSONException e ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        UserProcessor up = new UserProcessor(dbSession, httpSessionState);
        User groupOwner;
        try {
            groupOwner = up.getUser(httpSessionState.getUserId());
        } catch ( Exception e ) {
            try {
                UtilForREST.addErrorInfo(response, up.getMessage());
            } catch ( JSONException e1 ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        if ( groupOwner == null || !up.succeeded() ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            //TODO: Discuss - This should always work. Shall we therefore really pass the processor
            //error to the user, or rather send a default server error instead?
            try {
                UtilForREST.addErrorInfo(response, up.getMessage());
            } catch ( JSONException e ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        String groupName;
        int initialMembers;
        try {
            groupName = fullRequest.getJSONObject("data").getString("groupName");
            initialMembers = fullRequest.getJSONObject("data").getInt("initialMembers");
        } catch ( JSONException e ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            try {
                UtilForREST.addErrorInfo(response, Key.COMMAND_INVALID);
            } catch ( JSONException e1 ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        UserGroupProcessor ugp = new UserGroupProcessor(dbSession, httpSessionState, this.isPublicServer);
        UserGroup userGroup = ugp.createGroup(groupName, groupOwner, initialMembers);

        Statistics.info(cmd, "success", ugp.succeeded() && userGroup != null);

        try {
            UtilForREST.addResultInfo(response, ugp);
        } catch ( JSONException e ) {
            // Can not happen
        }

        if ( !ugp.succeeded() ) {
            dbSession.rollback();
        } else if ( userGroup != null ) {
            try {
                response.putOpt("userGroup", userGroup.toListJSON());
            } catch ( JSONException e ) {
                //Should not happen, but if it does there is simply no property added to the JSON
                //TODO: Log that!
            }
        }

        dbSession.close();
        return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
    }

    /**
     * TODO: Check for isPublic on whether or not a global user can have a group or not
     *
     * @param dbSession
     * @param fullRequest
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleteUserGroup")
    public Response deleteUserGroup(@OraData DbSession dbSession, JSONObject fullRequest) {
        String cmd = "deleteUserGroup";
        JSONObject response = new JSONObject();

        try {
            response.put("cmd", cmd);
        } catch ( JSONException e ) {
            // Can not happen, because the key is neither null, nor is the value numeric and infinite
            e.printStackTrace();
        }

        UserGroupController.LOG.info("command is: " + cmd);

        HttpSessionState httpSessionState = UtilForREST.handleRequestInit(UserGroupController.LOG, fullRequest);

        if ( !httpSessionState.isUserLoggedIn() ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            try {
                UtilForREST.addErrorInfo(response, Key.USER_ERROR_NOT_LOGGED_IN);
            } catch ( JSONException e ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        UserProcessor up = new UserProcessor(dbSession, httpSessionState);
        User groupOwner;
        try {
            groupOwner = up.getUser(httpSessionState.getUserId());
        } catch ( Exception e ) {
            try {
                UtilForREST.addErrorInfo(response, up.getMessage());
            } catch ( JSONException e1 ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        if ( groupOwner == null || !up.succeeded() ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            //TODO: Discuss - This should always work. Shall we therefore really pass the processor
            //error to the user, or rather send a default server error instead?
            try {
                UtilForREST.addErrorInfo(response, up.getMessage());
            } catch ( JSONException e ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        String groupName;
        boolean deleteMembers = false;
        try {
            groupName = fullRequest.getJSONObject("data").getString("groupName");
            deleteMembers = fullRequest.getJSONObject("data").getBoolean("deleteMembers");
        } catch ( JSONException e ) {
            UserGroupController.LOG.error("Invalid command: " + cmd);
            try {
                UtilForREST.addErrorInfo(response, Key.COMMAND_INVALID);
            } catch ( JSONException e1 ) {
                // Can not happen
            }
            return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
        }

        UserGroupProcessor ugp = new UserGroupProcessor(dbSession, httpSessionState, this.isPublicServer);
        ugp.deleteGroup(groupName, groupOwner, deleteMembers);

        Statistics.info(cmd, "success", ugp.succeeded());

        try {
            UtilForREST.addResultInfo(response, ugp);
        } catch ( JSONException e ) {
            // Can not happen
        }

        if ( !ugp.succeeded() ) {
            dbSession.rollback();
        }

        dbSession.close();
        return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
    }

    /**
     * This method is here and not in the user controller, because the group owner needs to be logged in in order to run it.
     *
     * @param dbSession
     * @param fullRequest
     * @return
     * @throws Exception
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/setUserGroupMemberDefaultPassword")
    public Response setUserGroupMemberDefaultPassword(@OraData DbSession dbSession, JSONObject fullRequest) throws Exception {
        JSONObject response = new JSONObject();
        HttpSessionState httpSessionState = UtilForREST.handleRequestInit(UserGroupController.LOG, fullRequest);
        try {
            Map<String, String> responseParameters = new HashMap<>();
            JSONObject requestData = fullRequest.getJSONObject("data");
            String cmd = "setUserGroupMemberDefaultPassword";
            UserGroupController.LOG.info("command is: " + cmd);
            response.put("cmd", cmd);
            UserProcessor up = new UserProcessor(dbSession, httpSessionState);

            if ( !httpSessionState.isUserLoggedIn() ) {
                up.setStatus(ProcessorStatus.FAILED, Key.USER_ERROR_NOT_LOGGED_IN, responseParameters);
                UtilForREST.addResultInfo(response, up);
                return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
            }

            int groupOwnerId = httpSessionState.getUserId();
            User groupOwner = up.getUser(groupOwnerId);

            if ( groupOwner == null ) {
                //If the logged in user can not be found by ID, there is a server error, not the user processor "userName or id wrong" error
                up.setStatus(ProcessorStatus.FAILED, Key.SERVER_ERROR, responseParameters);
                UtilForREST.addResultInfo(response, up);
                return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
            }

            int groupMemberId = requestData.getInt("groupMemberId");

            if ( groupMemberId <= 0 ) {
                up.setStatus(ProcessorStatus.FAILED, Key.COMMAND_INVALID, responseParameters);
                UtilForREST.addResultInfo(response, up);
                return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
            }

            User groupMember = up.getUser(groupMemberId);

            if ( groupMember == null || !up.succeeded() ) {
                //Overwrite the user processor "userName or id wrong" status, so people can not use this resource to test if specific accounts exist
                up.setStatus(ProcessorStatus.FAILED, Key.COMMAND_INVALID, responseParameters);
                UtilForREST.addResultInfo(response, up);
                return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
            }

            UserGroup userGroup = groupMember.getGroup();

            if ( userGroup == null || userGroup.getOwner().getId() != groupOwnerId ) {
                up.setStatus(ProcessorStatus.FAILED, Key.COMMAND_INVALID, responseParameters);
                UtilForREST.addResultInfo(response, up);
                return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
            }

            //Set the userName as password.
            up.resetPassword(groupMember.getId(), groupMember.getAccount());

            //user processor will set status accordingly in the resetPassword method

            UtilForREST.addResultInfo(response, up);
        } catch ( Exception e ) {
            dbSession.rollback();
            String errorTicketId = Util.getErrorTicketId();
            UserGroupController.LOG.error("Exception. Error ticket: " + errorTicketId, e);
            UtilForREST.addErrorInfo(response, Key.SERVER_ERROR).append("parameters", errorTicketId);
        } finally {
            if ( dbSession != null ) {
                dbSession.close();
            }
        }
        return UtilForREST.responseWithFrontendInfo(response, httpSessionState, this.brickCommunicator);
    }
}
