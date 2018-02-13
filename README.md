= gcvp

This project is design to be a cron-job that stop (scale down) dev project. 
1 - It will look for all the deploy and deploymentconfig
2.1 - If label io.shyrka.gcvp/scaledown=false, then nothing
2.2 - If label current date is before the "io.shyrka.erebus/end-date" value,  then nothing
2.3 - otherwise it scale down the dc or deploy

Sisyphus validate projet conformity
3. It will look for configmap with label io.shyrka.sisyphus/start

== Env


== Env

mvn io.fabric8:fabric8-maven-plugin:build

kubectl create configmap shyrka --from-literal=agent=true
kubectl run gcvp --image=kanedafromparis/gcvp:1.0-SNAPSHOT --restart=OnFailure


== LABEL

We use label on the shirka configmap instead of using info from that config, because it will be possible 

io.shyrka.erebus for project management

io.shyrka.gcvp for the cleaning-bot

io.shyrka.gcvp/scaledown=false||
io.shyrka.gcvp/backup.last=false||
io.shyrka.gcvp/backup=true||
io.shyrka.gcvp/keepalive=true||


io.shyrka.erebus/start-date=20180211||
io.shyrka.erebus/end-date=20180728||
io.shyrka.erebus/product.description=...||
io.shyrka.erebus/product.owner=20180728||

io.shyrka.erebus/team.watchers=20180728||
io.shyrka.erebus/team.validators||
io.shyrka.erebus/team.owners||
io.shyrka.erebus/team.owners.last.validation=20180728||
io.shyrka.erebus/team.owners.last.validation=20180728

io.shyrka.erebus/$(stack)-version



to-remove
filesystem.size


--
