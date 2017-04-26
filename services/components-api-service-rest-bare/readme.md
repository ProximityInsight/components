Components API Service REST bare
===
This is a module packages the rest service module alone, without any components.

This can be used with a remote component maven repository. 
## configuration
To configure the service you'll have to 

* provide a the list of components you want to register in the service .
You should be placing a file name **components.list** in the _config_ folder. (see the in the [component maven config artifact] (https://artifacts-zl.talend.com/nexus/content/repositories/TalendP2UnzipOpenSourceSnapshot/org/talend/components/components-maven-repo/0.18.0-SNAPSHOT/components-maven-repo-0.18.0-SNAPSHOT.jar-unzip/) for an example).
* You also need to specify where to find those components
there is a standard **settings.xml** file located in the _config_ folder that can be adjusted to add some remote maven repository to download the components from. You can also settup some credentials if required.

## artifact produced
* a zip file which contains the service the start scripts and the minimum configuration files to launch it without any components. (see above to configure it with some components)
* a jar (classifier:config) that contains startup scripts and configuration files to be used by other modules. 