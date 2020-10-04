# Usage: ./run_client.sh [<server_hostname> [<server_rmiobject>]]

java -Djava.security.policy=java.policy -cp ../Middleware/MwInterface.jar:. Client.RMIClient $1 $2
help