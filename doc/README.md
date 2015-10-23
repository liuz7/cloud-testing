$ export FLEETCTL_TUNNEL=127.0.0.1:2222
$ ssh-add ~/.vagrant.d/insecure_private_key
$ fleetctl list-machines

DISCOVERY_TOKEN=`curl -s https://discovery.etcd.io/new` && perl -i -p -e "s@discovery: https://discovery.etcd.io/\w+@discovery: $DISCOVERY_TOKEN@g" user-data