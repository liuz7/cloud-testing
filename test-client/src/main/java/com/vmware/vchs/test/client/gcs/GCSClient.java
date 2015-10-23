package com.vmware.vchs.test.client.gcs;

/**
 * Created by georgeliu on 15/7/15.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;
import com.vmware.vchs.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.net.URLConnection;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

public class GCSClient {

    private static Properties properties;
    private static Storage storage;

    private static final String PROJECT_ID_PROPERTY = "project.id";
    private static final String APPLICATION_NAME_PROPERTY = "application.name";
    private static final String ACCOUNT_ID_PROPERTY = "account.id";
    private static final String PRIVATE_KEY_PATH_PROPERTY = "private.key.path";
    private static final Logger logger = LoggerFactory.getLogger(GCSClient.class);
    private static final String BUCKET_NAME = "bucket.name";

    /**
     * Uploads a file to a bucket. Filename and content type will be based on
     * the original file.
     *
     * @param bucketName Bucket where file will be uploaded
     * @param filePath   Absolute path of the file to upload
     * @throws Exception
     */
    public void uploadFile(String bucketName, String filePath)
            throws Exception {

        Storage storage = getStorage();

        StorageObject object = new StorageObject();
        object.setBucket(bucketName);

        File file = new File(filePath);

        InputStream stream = new FileInputStream(file);
        try {
            String contentType = URLConnection
                    .guessContentTypeFromStream(stream);
            InputStreamContent content = new InputStreamContent(contentType,
                    stream);

            Storage.Objects.Insert insert = storage.objects().insert(
                    bucketName, null, content);
            insert.setName(file.getName());

            insert.execute();
        } finally {
            stream.close();
        }
    }

    public void downloadFile(String bucketName, String fileName, String destinationDirectory) throws Exception {

        File directory = new File(destinationDirectory);
        if (!directory.isDirectory()) {
            throw new Exception("Provided destinationDirectory path is not a directory");
        }
        File file = new File(directory.getAbsolutePath() + "/" + fileName);

        Storage storage = getStorage();

        Storage.Objects.Get get = storage.objects().get(bucketName, fileName);
        FileOutputStream stream = new FileOutputStream(file);
        try {
            get.executeAndDownloadTo(stream);
        } finally {
            stream.close();
        }
    }

    /**
     * Deletes a file within a bucket
     *
     * @param bucketName Name of bucket that contains the file
     * @param fileName   The file to delete
     * @throws Exception
     */
    public void deleteFile(String bucketName, String fileName)
            throws Exception {

        Storage storage = getStorage();

        storage.objects().delete(bucketName, fileName).execute();
    }

    /**
     * Creates a bucket
     *
     * @param bucketName Name of bucket to create
     * @throws Exception
     */
    public void createBucket(String bucketName) throws Exception {

        Storage storage = getStorage();

        Bucket bucket = new Bucket();
        bucket.setName(bucketName);

        storage.buckets().insert(
                getProperties().getProperty(PROJECT_ID_PROPERTY), bucket).execute();
    }

    /**
     * Deletes a bucket
     *
     * @param bucketName Name of bucket to delete
     * @throws Exception
     */
    public void deleteBucket(String bucketName) throws Exception {

        Storage storage = getStorage();

        storage.buckets().delete(bucketName).execute();
    }

    /**
     * Lists the objects in a bucket
     *
     * @param bucketName bucket name to list
     * @return Array of object names
     * @throws Exception
     */
    public List<String> listBucket(String bucketName) throws Exception {

        Storage storage = getStorage();

        List<String> list = new ArrayList<String>();

        List<StorageObject> objects = storage.objects().list(bucketName).execute().getItems();
        if (objects != null) {
            for (StorageObject o : objects) {
                list.add(o.getName());
            }
        }

        return list;
    }

    //009639e3-1ac2-464a-badd-946754c0ece9_20150628014145_2363.Log.bak (83456 bytes)
    public int getSizeOfFile(String fileName) {
        BigInteger size = null;
        try {
            Storage storage = getStorage();
            Storage.Objects.Get object = storage.objects().get(getProperties().getProperty(BUCKET_NAME), fileName);
            StorageObject storageObject = object.execute();
            size = storageObject.getSize();
            logger.info(storageObject.getName());
            logger.info(String.valueOf(size));
        } catch (Exception e) {
            Utils.getStackTrace(e);
        }
        return checkNotNull(size.intValue());
    }

    /**
     * List the buckets with the project
     * (Project is configured in properties)
     *
     * @return
     * @throws Exception
     */
    public List<String> listBuckets() throws Exception {

        Storage storage = getStorage();

        List<String> list = new ArrayList<String>();

        List<Bucket> buckets = storage.buckets().list(getProperties().getProperty(PROJECT_ID_PROPERTY)).execute().getItems();
        if (buckets != null) {
            for (Bucket b : buckets) {
                list.add(b.getName());
            }
        }

        return list;
    }

    private static Properties getProperties() throws Exception {

        if (properties == null) {
            properties = new Properties();
            InputStream stream = GCSClient.class
                    .getResourceAsStream("/gcs/cloudstorage.properties");
            try {
                properties.load(stream);
            } catch (IOException e) {
                throw new RuntimeException(
                        "cloudstorage.properties must be present in classpath",
                        e);
            } finally {
                stream.close();
            }
        }
        return properties;
    }

    private Storage getStorage() throws Exception {

        if (storage == null) {

            HttpTransport httpTransport = new NetHttpTransport.Builder().doNotValidateCertificate().build();
            JsonFactory jsonFactory = new JacksonFactory();

            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_READ_ONLY);
            PrivateKey privateKey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), this.getClass().getResourceAsStream(getProperties().getProperty(
                    PRIVATE_KEY_PATH_PROPERTY)), "notasecret", "privatekey", "notasecret");
            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId(
                            getProperties().getProperty(ACCOUNT_ID_PROPERTY))
                    /*.setServiceAccountPrivateKeyFromP12File(
                            new File(getProperties().getProperty(
                                    PRIVATE_KEY_PATH_PROPERTY)).getCanonicalFile())*/
                    .setServiceAccountPrivateKey(privateKey)
                    .setServiceAccountScopes(scopes).build();

            storage = new Storage.Builder(httpTransport, jsonFactory,
                    credential).setApplicationName(
                    getProperties().getProperty(APPLICATION_NAME_PROPERTY))
                    .build();
        }

        return storage;
    }

    public static void main(String[] args) throws Exception {

        System.out.println(new GCSClient().getSizeOfFile("009639e3-1ac2-464a-badd-946754c0ece9_20150628014145_2363.Log.bak"));

    }

}
