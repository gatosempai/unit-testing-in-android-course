package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.UpdateUsernameUseCaseSync.UseCaseResult;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "userName";
    UpdateUsernameUseCaseSync SUT;
    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSyncMock;
    UsersCache usersCacheMock;
    EventBusPoster eventBusPosterMock;

    @Before
    public void setUp() throws Exception {
        updateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusPosterMock);
        success();
    }

    @Test
    public void updateUser_success_userNamePassedToEndpoint() throws NetworkErrorException {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verify(updateUsernameHttpEndpointSyncMock, times(1))
                .updateUsername(argumentCaptor.capture(), argumentCaptor.capture());
        List<String> captures = argumentCaptor.getAllValues();
        assertThat(captures.get(0), is(USER_ID));
        assertThat(captures.get(1), is(USER_NAME));
    }

    @Test
    public void updateUser_success_userNameUpdated() {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verify(usersCacheMock).cacheUser(argumentCaptor.capture());
        User result = argumentCaptor.getValue();
        assertThat(result.getUserId(), is(USER_ID));
        assertThat(result.getUsername(), is(USER_NAME));
    }

    @Test
    public void updateUser_generalError_userNameNotUpdated() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUser_authError_userNameNotUpdated() throws NetworkErrorException {
        authError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUser_serverError_userNameNotUpdated() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUser_networkError_userNameNotUpdated() throws NetworkErrorException {
        networkError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void updateUser_success_userNameInEventPosted() {
        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_NAME, USER_ID);
        verify(eventBusPosterMock).postEvent(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUser_generalError_userNameNotInEventPosted() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_NAME, USER_ID);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUser_authError_userNameNotInEventPosted() throws NetworkErrorException {
        authError();
        SUT.updateUsernameSync(USER_NAME, USER_ID);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUser_serverError_userNameNotInEventPosted() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(USER_NAME, USER_ID);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUser_networkError_userNameNotInEventPosted() throws NetworkErrorException {
        networkError();
        SUT.updateUsernameSync(USER_NAME, USER_ID);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void updateUser_success_successReturned() {
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUser_generalError_failureReturned() throws NetworkErrorException {
        generalError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUser_authError_failureReturned() throws NetworkErrorException {
        authError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUser_serverError_failureReturned() throws NetworkErrorException {
        serverError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.FAILURE));
    }

    @Test
    public void updateUser_networkError_networkErrorReturned() throws NetworkErrorException {
        networkError();
        UseCaseResult result = SUT.updateUsernameSync(USER_ID, USER_NAME);
        assertThat(result, is(UseCaseResult.NETWORK_ERROR));
    }


    private void success() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, USER_NAME));
    }

    private void generalError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws NetworkErrorException {
        when(updateUsernameHttpEndpointSyncMock.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws NetworkErrorException {
        doThrow(new NetworkErrorException()).when(updateUsernameHttpEndpointSyncMock)
                .updateUsername(any(String.class), any(String.class));
    }
}