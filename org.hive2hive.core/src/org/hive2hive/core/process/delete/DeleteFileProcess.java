package org.hive2hive.core.process.delete;

import java.io.File;

import org.apache.log4j.Logger;
import org.hive2hive.core.file.FileManager;
import org.hive2hive.core.log.H2HLoggerFactory;
import org.hive2hive.core.model.FileTreeNode;
import org.hive2hive.core.model.UserProfile;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.process.Process;
import org.hive2hive.core.process.common.File2MetaFileStep;
import org.hive2hive.core.process.common.get.GetUserProfileStep;
import org.hive2hive.core.security.UserCredentials;

/**
 * 1. delete the file on disk if it is still here
 * 2. get user profile and find the {@link FileTreeNode} in there. Check if write-access to this file
 * 3. get the meta file / folder
 * 4. delete all chunks of all versions from the DHT
 * 5. delete the meta file / folder
 * 6. get the parent meta file
 * 7. update the parent meta file
 * 8. remove tree node from user profile and update it
 * 9. notify other clients
 * 
 * @author Nico
 * 
 */
public class DeleteFileProcess extends Process {

	private final static Logger logger = H2HLoggerFactory.getLogger(DeleteFileProcess.class);
	private final DeleteFileProcessContext context;

	/**
	 * Default constructor that also deletes the file on disc.
	 * 
	 * @param file the file to delete
	 * @param fileManager the file manager
	 * @param networkManager the network manager, connected to the H2H network
	 * @param credentials the credentials of the user
	 * @throws IllegalArgumentException if the file cannot be deleted
	 */
	public DeleteFileProcess(File file, FileManager fileManager, NetworkManager networkManager,
			UserCredentials credentials) throws IllegalArgumentException {
		super(networkManager);
		logger.info("Deleting file/folder from the DHT");

		// verify if the file can be deleted
		verify(file);

		context = new DeleteFileProcessContext(this, fileManager, file.isDirectory(), credentials);

		// start by deleting the file
		setNextStep(new DeleteFileOnDiskStep(file));
	}

	/**
	 * Use this constructor to apply a file deletion during the absence of a user.
	 * 
	 * @param fileNode the file node in the user profile that needs to be deleted
	 * @param fileManager the file manager
	 * @param networkManager the network manager, connected to the H2H network
	 * @param credentials the credentials of the user
	 * @throws IllegalArgumentException if the file cannot be deleted
	 */
	public DeleteFileProcess(FileTreeNode fileNode, FileManager fileManager, NetworkManager networkManager,
			UserCredentials credentials) throws IllegalArgumentException {
		super(networkManager);
		logger.info("Deleting file/folder from the DHT");
		context = new DeleteFileProcessContext(this, fileManager, fileNode.isFolder(), credentials);

		File2MetaFileStep file2MetaStep = new File2MetaFileStep(fileNode, fileManager, context, context,
				new DeleteChunkStep());
		GetUserProfileStep getUserProfileStep = new GetUserProfileStep(credentials, file2MetaStep, context);
		setNextStep(getUserProfileStep);
	}

	private void verify(File file) throws IllegalArgumentException {
		if (file == null) {
			throw new IllegalArgumentException("File may not be null");
		}

		if (file.isDirectory() && file.listFiles().length > 0) {
			throw new IllegalArgumentException("Folder is not empty");
		}
	}

	/**
	 * May be used when the user profile is already existent. Thus, the step getting and decrypting the user
	 * profile can be omitted
	 * 
	 * @param userProfile the recent user profile
	 */
	public void setUserProfile(UserProfile userProfile) {
		context.setUserProfile(userProfile);
	}

	@Override
	public DeleteFileProcessContext getContext() {
		return context;
	}
}