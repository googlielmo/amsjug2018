package neil.demo.amsjug2018;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Job;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Add some custom commands to the Spring Shell CLI.
 * </p>
 */
@ShellComponent
@Slf4j
public class MyCLI {
	
    private HazelcastInstance hazelcastInstance;
    private JetInstance jetInstance;
    private String bootstrapServers;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public MyCLI(JetInstance jetInstance,
                 @Value("${bootstrap-servers}") String bootstrapServers,
                 @Value("${my.prompt}") String myPrompt) {
    	this.jetInstance = jetInstance;
        this.hazelcastInstance = jetInstance.getHazelcastInstance();
        this.bootstrapServers = bootstrapServers;

        // Initialise all maps
        for (String iMapName : Constants.IMAP_NAMES) {
            log.info("Initialize IMap '{}'", iMapName);
            this.hazelcastInstance.getMap(iMapName);
        }

        // Initialise all topics, and listener on each
        for (String iTopicName : Constants.ITOPIC_NAMES) {
            log.info("Initialize ITopic '{}'", iTopicName);
            ITopic iTopic = this.hazelcastInstance.getTopic(iTopicName);
            iTopic.addMessageListener(new MyTopicListener());
        }
    }

    /**
     * <p>Find and list all Jet jobs, running or completed, by name.
     * </p>
     * <p>In Jet, job names can be null or duplicated, but in this
     * demo they're unique and {@code @NonNull} so we can simplify
     * this logic using that assumption.
     * </p>
     *
     * @throws Exception
     */
    @ShellMethod(key = "JOBS", value = "List Jet jobs")
    public void jobs() throws Exception {
    	// Alphabetical order
        Set<String> jobNames = 
        		this.jetInstance.getJobs()
        		.stream()
        		.map(job -> job.getName())
        		.collect(Collectors.toCollection(TreeSet::new));
        
        jobNames
        .stream()
        .forEach(jobName -> {
        	Job job = this.jetInstance.getJob(jobName);
        	
        	System.out.printf("Job name '%s' id '%d' status '%s'%n",
        			job.getName(), job.getId(), job.getStatus());
        });

        System.out.printf("[%d job%s]%n", jobNames.size(), (jobNames.size() == 1 ? "" : "s"));
        System.out.println("");
    }
    
    /**
     * <p>List the maps in our cluster.</p>
     * <p>Hide the internal ones beginning "@{code __jet}" from the output.</p>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ShellMethod(key = "LIST", value = "List map entries")
    public void listIMaps() {
        Set<String> iMapNames = this.hazelcastInstance.getDistributedObjects().stream()
        		.filter(distributedObject -> distributedObject instanceof IMap)
        		.filter(distributedObject -> !distributedObject.getName().startsWith(Jet.INTERNAL_JET_OBJECTS_PREFIX))
        		.map(distributedObject -> distributedObject.getName()).collect(Collectors.toCollection(TreeSet::new));

        iMapNames.stream().forEach(name -> {
            IMap<?, ?> iMap = this.hazelcastInstance.getMap(name);

            System.out.println("");
            System.out.printf("IMap: '%s'%n", name);

            // Sort if possible
            Set<?> keys = iMap.keySet();
            if (!keys.isEmpty() && keys.iterator().next() instanceof Comparable) {
                keys = new TreeSet(keys);
            }

            keys.stream().forEach(key -> {
                System.out.printf("    -> '%s' -> %s%n", key, iMap.get(key));
            });

            System.out.printf("[%d entr%s]%n", iMap.size(), (iMap.size() == 1 ? "y" : "ies"));
        });

        System.out.println("");
        System.out.printf("[%d IMap%s]%n", iMapNames.size(), (iMapNames.size() == 1 ? "" : "s"));
        System.out.println("");
    }

    /**
     * <p>Request the moving average job be started, but posting
     * a <i>(noun, params)</i> pair to the map that the command
     * listener is observing.
     * </p>
     * <p>The params include the location of the Kafka servers
     * for the job to use.
     * </p>
     * 
     * @return
     */
    @ShellMethod(key = "MOVING_AVERAGE_START", value = "Start moving average job")
    public String movingAverageStart() {
        IMap<String, List<String>> commandMap = this.hazelcastInstance.getMap(Constants.IMAP_NAME_COMMAND);

        String noun = Constants.COMMAND_NOUN_MOVING_AVERAGE;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_START);
        params.add(this.bootstrapServers);

        commandMap.put(noun, params);

        return String.format("Requested %s job '%s' with %s", params.get(0), noun, params.get(1));
    }

    /**
     * <p>Request the moving average job be stopped, but posting
     * a <i>(noun, verb)</i> pair to the map that the command
     * listener is observing.
     * </p>
     * 
     * @return
     */
    @ShellMethod(key = "MOVING_AVERAGE_STOP", value = "Stop moving average job")
    public String movingAverageStop() {
        IMap<String, List<String>> commandMap = this.hazelcastInstance.getMap(Constants.IMAP_NAME_COMMAND);

        String noun = Constants.COMMAND_NOUN_MOVING_AVERAGE;

        List<String> params = new ArrayList<>();
        params.add(Constants.COMMAND_VERB_STOP);

        commandMap.put(noun, params);

        return String.format("Requested %s job '%s'", params.get(0), noun);
    }

}

