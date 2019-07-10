package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";
    FetchUserProfileUseCaseSync fetchUserProfileUseCaseSync;
    UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;
    UsersCacheTd usersCacheTd;

    @Before
    public void setUp() throws Exception {
        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        usersCacheTd = new UsersCacheTd();
        fetchUserProfileUseCaseSync = new FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd,
                usersCacheTd);
    }

    @Test
    public void fetchUserSync_success() {
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(userProfileHttpEndpointSyncTd.userId, is(USER_ID));
    }

    @Test
    public void fetchUserSync_success_userCachedReturn() {
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        User cachedUser = usersCacheTd.getUser(USER_ID);
        assertThat(cachedUser.getUserId(), is(USER_ID));
        assertThat(cachedUser.getFullName(), is(FULL_NAME));
        assertThat(cachedUser.getImageUrl(), is(IMAGE_URL));
    }

    @Test
    public void fetchUserSync_generalError_userNotCachedReturn() {
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserSync_authError_userNotCachedReturn() {
        userProfileHttpEndpointSyncTd.isAuthError = true;
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserSync_serverError_userNotCachedReturn() {
        userProfileHttpEndpointSyncTd.isServerError = true;
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(usersCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void fetchUserSync_success_successReturned() {
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        User cachedUser = usersCacheTd.getUser(USER_ID);
        assertThat(cachedUser, is(CoreMatchers.<User>instanceOf(User.class)));
    }

    @Test
    public void fetchUserSync_serverError_failureReturned() {
        userProfileHttpEndpointSyncTd.isServerError = true;
        UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserSync_authError_failureReturned() {
        userProfileHttpEndpointSyncTd.isAuthError = true;
        UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserSync_generalError_failureReturned() {
        userProfileHttpEndpointSyncTd.isGeneralError = true;
        UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserSync_networkError_failureReturned() {
        userProfileHttpEndpointSyncTd.isNetworkError = true;
        UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }

    // ---------------------------------------------------------------------------------------------
    // Helper classes

    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        public String userId;
        public boolean isGeneralError;
        public boolean isAuthError;
        public boolean isServerError;
        public boolean isNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            this.userId = userId;
            if (isGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR,
                        "", "", "");
            } else if (isAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR,
                        "", "", "");
            } else if (isServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR,
                        "", "", "");
            } else if (isNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID,
                        FULL_NAME, IMAGE_URL);
            }
        }
    }

    private static class UsersCacheTd implements UsersCache {

        User user;

        @Override
        public void cacheUser(User user) {
            this.user = user;
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            if (user != null && userId.equals(user.getUserId())) return user;
            else return null;
        }
    }
}