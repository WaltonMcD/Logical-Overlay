cat servers.txt | while read line; do
   exec $line
done

