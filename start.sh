# USAGE: ./start [Connections] [CSU eName]

MAX_CONN=$1    # editable
USERNAME=$2    # editable
NODE_DELAY='sleep 10;'     # editable. How long you have to setup-overlay before nodes attempt to connect to Registry.
JAR_FILE="Homework-1/build/libs/Homework-1.jar"
SERVER=''
PORT='22222'
REGISTRY_SCRIPT='java -jar '"$JAR_FILE"' cs455.overlay.Main server '"$PORT"' '"$MAX_CONN"''
NODE_SCRIPT=''

COMMAND='gnome-terminal'
counter=0

for i in `cat machines.txt`
do
    # Add 1 to not count the server as a connection.
    if [ $counter -eq $(($MAX_CONN+1)) ]; then
        break
    fi

    echo "Logging into $i."

	if [ $counter -eq 0 ]; then
	    SERVER="$i"
	    option='--window --title="'$i'" --command="ssh -t '$USERNAME'@'$SERVER'.cs.colostate.edu '$REGISTRY_SCRIPT' '$SHELL'"'
	else
	    NODE_SCRIPT="$NODE_DELAY java -jar Homework-1/build/libs/Homework-1.jar cs455.overlay.Registry node $SERVER.cs.colostate.edu $PORT"
	    option='--tab --title="'$i'" --command="ssh -t '$USERNAME'@'$i'.cs.colostate.edu '$NODE_SCRIPT' '$SHELL'"'
	fi

	COMMAND+=" $option"
	((counter++))
done

eval $COMMAND
