package com.vmware.vchs.base.impl;

import com.vmware.vchs.helper.IamAuthInfo;
import com.vmware.vchs.link.IamAPILinkBuilder;
import com.vmware.vchs.link.LinkBuilder;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.iamRole.*;
import com.vmware.vchs.model.portal.instance.*;
import com.vmware.vchs.model.portal.networks.ListVdcIpsResponse;
import com.vmware.vchs.model.portal.networks.UpdateConnectionRequest;
import com.vmware.vchs.model.portal.services.Service;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.model.portal.snapshot.ListSnapshotResponse;
import com.vmware.vchs.test.client.model.portal.PortalResponseErrorHandler;
import com.vmware.vchs.test.client.rest.RestClient;
import com.vmware.vchs.test.config.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by georgeliu on 15/3/10.
 */
public class TestClient {

    protected static DBaasRestClient restClient;

    public TestClient(String baseUrl) {
        this.restClient = new DBaasRestClient(baseUrl);
        restClient.loadConverters("com.vmware.vchs.converter");
        restClient.setErrorHandler(new PortalResponseErrorHandler());
    }

    public TestClient() {
        this.restClient = new DBaasRestClient();
        restClient.loadConverters("com.vmware.vchs.converter");
        restClient.setErrorHandler(new PortalResponseErrorHandler());
    }

    public TestClient(Configuration configuration) {
        this.restClient = new DBaasRestClient(configuration);
        restClient.loadConverters("com.vmware.vchs.converter");
        restClient.setErrorHandler(new PortalResponseErrorHandler());
    }

    
    public AsyncResponse restoreFromSnapshotInstance(RestoreFromSnapshotRequest createInstanceRequest) {
        return restClient.postForObject(LinkBuilder.restoreFromSnapshotPath(createInstanceRequest.getSnapshotId()), createInstanceRequest, AsyncResponse.class);
    }

    public IamAuthInfo getIamAuthInfo() {
        return restClient.getIamAuthInfo();
    }

    
    public void setAuthentication(String username, String password) {
        restClient.setAuthentication(username, password);
    }

    
    public RestClient getRestClient() {
        return checkNotNull(this.restClient);
    }

    
    public AsyncResponse createDBInstance(CreateInstanceRequest createInstanceRequest) {
        return restClient.postForObject(LinkBuilder.getInstancePath(), createInstanceRequest, AsyncResponse.class);
    }

    
    public AsyncResponse launchPitrTask(CreatePitrRequest createPitrRequest, String instanceId) {
        return restClient.postForObject(LinkBuilder.launchPitrPath(instanceId), createPitrRequest, AsyncResponse.class);
    }

    
    public ResponseEntity<AsyncResponse> createDBInstanceEntity(CreateInstanceRequest createInstanceRequest) {
        return restClient.postForEntity(LinkBuilder.getInstancePath(), createInstanceRequest, AsyncResponse.class);
    }

    public ListenableFuture<ResponseEntity<AsyncResponse>> createAsyncDBInstanceEntity(CreateInstanceRequest createInstanceRequest) {
        return restClient.postForAsyncEntity(LinkBuilder.getInstancePath(), createInstanceRequest, AsyncResponse.class);
    }

    /*
    public void deleteDBInstance(String instanceId) {
        restClient.deleteForObject(LinkBuilder.getInstanceIdPath(instanceId), AsyncResponse.class);
    }*/

    
    public GetInstanceResponse getDBInstance(String instanceId) {
        return restClient.getForObject(LinkBuilder.getInstanceIdPath(instanceId), GetInstanceResponse.class);
    }

    
    public ListenableFuture<ResponseEntity<GetInstanceResponse>> getAsyncDBInstanceEntity(String instanceId) {
        return restClient.getForAsyncEntity(LinkBuilder.getInstanceIdPath(instanceId), GetInstanceResponse.class);
    }

    
    public ResponseEntity<GetInstanceResponse> getDBInstanceEntity(String instanceId) {
        return restClient.getForEntity(LinkBuilder.getInstanceIdPath(instanceId), GetInstanceResponse.class);
    }

    
    public ResponseEntity<AsyncResponse> deleteForDBInstanceEntity(String instanceId) {
        return restClient.deleteForEntity(LinkBuilder.getInstanceIdPath(instanceId), AsyncResponse.class);
    }

    
    public AsyncResponse deleteForDBInstance(String instanceId) {
        return restClient.deleteForObject(LinkBuilder.getInstanceIdPath(instanceId), AsyncResponse.class);
    }

    
    public ListResponse listDBInstances() {
        return restClient.getForObject(LinkBuilder.getInstancePath(), ListResponse.class);
    }

    
    public AsyncResponse updateDBInstance(String instanceId, UpdateInstanceRequest updateInstanceRequest) {
        return restClient.putForObject(LinkBuilder.getInstanceIdPath(instanceId), updateInstanceRequest, AsyncResponse.class);
    }

    
    public ResponseEntity<AsyncResponse> updateDBInstanceEntity(String instanceId, UpdateInstanceRequest updateInstanceRequest) {
        return restClient.putForEntity(LinkBuilder.getInstanceIdPath(instanceId), updateInstanceRequest, AsyncResponse.class);
    }

    
    public GetInstanceResponse getSnapshotDBInstance(String snapshotId) {
        return restClient.getForObject(LinkBuilder.getSnapshotIdPath(snapshotId), GetInstanceResponse.class);
    }

    
    public AsyncResponse createSnapshot(CreateSnapshotRequest createSnapshotRequest) {
        return restClient.postForObject(LinkBuilder.getSnapshotPath(), createSnapshotRequest, AsyncResponse.class);
    }

    
    public ResponseEntity<AsyncResponse> createSnapshotEntity(CreateSnapshotRequest createSnapshotRequest) {
        return restClient.postForEntity(LinkBuilder.getSnapshotPath(), createSnapshotRequest, AsyncResponse.class);
    }

    
    public ListenableFuture<ResponseEntity<AsyncResponse>> createAsyncSnapshotEntity(CreateSnapshotRequest createSnapshotRequest) {
        return restClient.postForAsyncEntity(LinkBuilder.getSnapshotPath(), createSnapshotRequest, AsyncResponse.class);
    }

    
    public void deleteSnapshot(String snapshotId) {
        restClient.deleteForObject(LinkBuilder.getSnapshotIdPath(snapshotId), AsyncResponse.class);
    }

    
    public GetSnapshotResponse getSnapshot(String snapshotId) {
        return restClient.getForObject(LinkBuilder.getSnapshotIdPath(snapshotId), GetSnapshotResponse.class);
    }

    
    public ListenableFuture<ResponseEntity<ListSnapshotResponse>> getAsyncSnapshotEntity(String snapshotId) {
        return restClient.getForAsyncEntity(LinkBuilder.getSnapshotIdPath(snapshotId), ListSnapshotResponse.class);
    }

    
    public ResponseEntity<ListSnapshotResponse> getSnapshotEntity(String snapshotId) {
        return restClient.getForEntity(LinkBuilder.getSnapshotIdPath(snapshotId), ListSnapshotResponse.class);
    }

    
    public ResponseEntity<ListSnapshotResponse> deleteForSnapshotEntity(String snapshotId) {
        return restClient.deleteForEntity(LinkBuilder.getSnapshotIdPath(snapshotId), ListSnapshotResponse.class);
    }

    
    public ListSnapshotResponse deleteForSnapshot(String snapshotId) {
        return restClient.deleteForObject(LinkBuilder.getSnapshotIdPath(snapshotId), ListSnapshotResponse.class);
    }

    
    public ListSnapshotResponse listSnapshot(int pageSize, int page) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put("pageSize", pageSize);
        query.put("page", page);
        return restClient.getForObject(LinkBuilder.getSnapshotPath(), query, ListSnapshotResponse.class);
    }

    public ListSnapshotResponse listSnapshot() {
        return listSnapshot(100, 1); //default page size 100 to list all snapshots
    }

    
    public void updateSnapshot(String snapshotId, CreateSnapshotRequest createSnapshotRequest) {
        restClient.put(LinkBuilder.getSnapshotIdPath(snapshotId), createSnapshotRequest);
    }

    
    public Service getServices() {
        return restClient.getForObject(LinkBuilder.getServicesPath(), Service.class);
    }

    
    public ListVdcIpsResponse getVdcIps() {
        return restClient.getForObject(LinkBuilder.getVdcIPsPath(), ListVdcIpsResponse.class);
    }

    
    public Connections getDataPath(String instanceId) {
        return restClient.getForObject(LinkBuilder.getInstanceDataPath(instanceId), Connections.class);
    }

    
    public DataPath updateConnection(String instanceId, UpdateConnectionRequest updateConnectionRequest) {
        return restClient.putForObject(LinkBuilder.getInstanceConnectionPath(instanceId), updateConnectionRequest, DataPath.class);
    }


    /**
     * Created by fanz on 5/5/15. TestClientImp for IAM-Role Testing
     */


    public GetRoleResponse getRolePermission() {
        String url = IamAPILinkBuilder.getRolePath();
        return restClient.getForObject(url, GetRoleResponse.class);
    }

    public GetRoleResponse getRolePermissionInstance(String instanceId) {
        String url = IamAPILinkBuilder.getRolePath();
        Map<String, Object> query = new HashMap<>();
        query.put("resourceId", instanceId);
        return restClient.getForObject(url, query, GetRoleResponse.class);
    }

    public GetUserResponse getAllUserGlobal() {
        String url = IamAPILinkBuilder.getUserPath();
        return restClient.getForObject(url, GetUserResponse.class);
    }

    public GetUserResponse getSearchUserGlobal(String userId) {
        String url = IamAPILinkBuilder.getUserPath();
        Map<String, Object> query = new HashMap<>();
        query.put("q", userId);
        return restClient.getForObject(url, query, GetUserResponse.class);
    }

    public GetUserRoleResponse getAllUserRoleGlobal() {
        String url = IamAPILinkBuilder.getAccessControlPath();
        return restClient.getForObject(url, GetUserRoleResponse.class);
    }

    public GetUserRoleResponse getUserRoleGlobal(String userId) {
        String url = IamAPILinkBuilder.getAccessControlPath();
        Map<String, Object> query = new HashMap<>();
        query.put("userId", userId);
        GetUserRoleResponse response = restClient.getForObject(url, query, GetUserRoleResponse.class);
        return response;
    }

    public GetUserRoleResponse getAllUserRoleInstance(String resourceId) {
        String url = IamAPILinkBuilder.getAccessControlPath();
        Map<String, Object> query = new HashMap<>();
        query.put("resourceId", resourceId);
        return restClient.getForObject(url, query, GetUserRoleResponse.class);
    }

    public GetUserRoleResponse getUserRoleInstance(String userId, String resourceId) {
        String url = IamAPILinkBuilder.getAccessControlPath();
        Map<String, Object> query = new HashMap<>();
        query.put("userId", userId);
        query.put("resourceId", resourceId);
        return restClient.getForObject(url, query, GetUserRoleResponse.class);
    }

    public UpdateRoleResponse getRoleUpdateGlobal(UpdateRoleRequest updateRoleRequest) {
        String url = IamAPILinkBuilder.getAccessControlPath();
        return restClient.putForObject(url, updateRoleRequest, UpdateRoleResponse.class);
    }

    public UpdateRoleResponse getRoleUpdateInstance(String resourceId, UpdateRoleRequest updateRoleRequest) {
        String url = IamAPILinkBuilder.getAccessControlPath();
        Map<String, Object> query = new HashMap<>();
        query.put("resourceId", resourceId);
        return restClient.putForObject(url, updateRoleRequest, query, UpdateRoleResponse.class);
    }


}
