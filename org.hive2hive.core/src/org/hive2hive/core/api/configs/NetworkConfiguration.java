package org.hive2hive.core.api.configs;

import java.net.InetAddress;

public class NetworkConfiguration implements INetworkConfiguration {

	private final String nodeID;
	private final boolean isMasterPeer;
	private final InetAddress bootstrapAddress;
	private final int bootstrapPort;

	private NetworkConfiguration(String nodeID, boolean isMasterPeer, InetAddress bootstrapAddress, int bootstrapPort) {
		this.bootstrapAddress = bootstrapAddress;
		this.isMasterPeer = isMasterPeer;
		this.nodeID = nodeID;
		this.bootstrapPort = bootstrapPort;
	}

	public static NetworkConfiguration create(String nodeID) {
		return new NetworkConfiguration(nodeID, true, null, -1);
	}

	public static NetworkConfiguration create(String nodeID, InetAddress bootstrapAddress) {
		return new NetworkConfiguration(nodeID, false, bootstrapAddress, -1);
	}
	
	public static NetworkConfiguration create(String nodeID, InetAddress bootstrapAddress, int bootstrapPort) {
		return new NetworkConfiguration(nodeID, false, bootstrapAddress, bootstrapPort);
	}

	@Override
	public String getNodeID() {
		return nodeID;
	}

	@Override
	public boolean isMasterPeer() {
		return isMasterPeer;
	}

	@Override
	public InetAddress getBootstrapAddress() {
		return bootstrapAddress;
	}

	@Override
	public int getBootstrapPort() {
		return bootstrapPort;
	}

}