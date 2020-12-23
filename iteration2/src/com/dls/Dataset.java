package com.dls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The Dataset class is main class of the project. It stores all other objects and instances.
 * @version iteration-2.0
 * @since 2020-12-01
 *
 */
public class Dataset {
    
    private int id;
    private String name;
    private int maxNumberOfLabels;
    private String inputPath;
    private Double consistencyCheckProbability;
    private ArrayList<Label> labels = new ArrayList<Label>(); // Make it limited
    private ArrayList<User> users = new ArrayList<User>();
    private ArrayList<Instance> instances = new ArrayList<Instance>();

    /*
     * Construct method of the Dataset class
     * @param   id                      unique id number of dataset
     * @param   name                    name of the dataset
     * @param   maxNumberOfLabels       maximum number of labels to assign a single instance
     * @return                          nothing
     */
    public Dataset(int id, String name, int maxNumberOfLabels, String inputPath) {
         setId(id);
         setName(name);
         setMaxNumberOfLabels(maxNumberOfLabels);
         //this.consistencyCheckProbability = consistencyCheckProbability;
         this.inputPath = inputPath;
         //@todo set input path method
    }
    /*
     * Sets the id of dataset
     * @todo Check if it used before
     * @param   id                      unique id number of dataset
     * @return                          nothing
     */
    private void setId(int id){
        this.id = id;
    }
    /*
     * Sets the name of dataset
     * @param   name                    name of dataset to set
     * @return                          nothing
     */
    private void setName(String name){
        this.name = name;
    }
    /*
     * Sets the maximum number of labels to asing a single instance
     * @param   maxNumberOfLabels       maximum number of label for a single instance
     * @return                          nothing
     */
    public void setMaxNumberOfLabels(int maxNumberOfLabels){
        this.maxNumberOfLabels = maxNumberOfLabels;
    } 
    /*
     * Gets dataset id
     * @return                          id of dataset
     */
    protected int getId(){
        return this.id;
    }
    /*
     * Gets dataset name
     * @return                          name of dataset
     */
    protected String getName(){
        return this.name;
    }
    /*
     * Gets maximum number of labels to assign a instance
     * @return                          MaxNumberOfLabels
     */
    protected int getMaxNumberOfLabels(){
        return this.maxNumberOfLabels;
    }
    /*
     * Returns list of instances of dataset
     * This function is used during the JSON reading process.
     * @return                          Array List of <Instance> objects
     */
    protected ArrayList<Instance> getInstances(){
        return this.instances;
    }
    /*
     * Returns list of labels of dataset
     * This function is used during the JSON reading process.
     * @return                          Array List of <Label> objects
     */
    protected ArrayList<Label> getLabels(){
        return this.labels;
    }
    /*
     * Finds and returns the <Label> object with the given text value.
     * @todo What if Label has not found?
     * @param   text                    text of label
     * @return                          <Label> object
     */
    protected Label getLabelByText(String text){
        for(Label label : this.labels){
            if(label.getText().equals(text)){
                return label;
            }
        }
        return new Label(0, text);
    }
    /*
     * Finds and returns the <Label> object with the given id value.
     * @todo What if Label has not found?
     * @param   int                     id of label
     * @return                          <Label> object
     */
    protected Label getLabelById(int id){
        for(Label label : this.labels){
            if(label.getId() == id){
                return label;
            }
        }
        return new Label(id, "");
    }

    protected String getInputPath(){
        return this.inputPath;
    }

    protected Float getUserCompletions(){
        Integer numberOfInstances = this.instances.size();
        Integer numberOfInstancesWithLabel = 0;
        for(Instance instance : this.instances){
            if(!instance.getAssignments().isEmpty()){
                numberOfInstancesWithLabel += 1;
            }
            numberOfInstances += 1;
        }
        return numberOfInstancesWithLabel.floatValue()/numberOfInstances.floatValue()*100;
    }

    protected HashMap<Label, Double> getLabelFrequencies(){
        HashMap<Label, Double> labelFrequencies = new HashMap<Label, Double>();
        for(Instance instance : this.instances){
            if(instance.getFinalLabel() != null){
                Label label = instance.getFinalLabel();
                if(labelFrequencies.containsKey(label)){
                    labelFrequencies.replace(label, labelFrequencies.get(label)+1.0);
                }else{
                    labelFrequencies.put(label, 1.0);
                }
            }
        }
        Map.Entry<Label, Double> maxEntry = null;
        Integer totalNumberOfLabels = this.getNumberOfInstances();
        for (Map.Entry<Label, Double> entry : labelFrequencies.entrySet()) {
            labelFrequencies.replace(entry.getKey(), entry.getValue()/totalNumberOfLabels*100);
        }
        return labelFrequencies;
    }


    protected int getNumberOfInstances(){
        return this.instances.size();
    }


    protected int getNumberOfUsers(){
        return this.users.size();
    }

    protected Double getCompletionPercentage(){
        Integer completedInstances = 0;
        for(Instance instance : this.instances){
            if(!instance.getAssignments().isEmpty()){
                completedInstances ++;
            }
        }
        return completedInstances.doubleValue() / this.instances.size() * 100.0;
    }

    protected HashMap<User, Double> getUserCompletionsPercentages(){
        HashMap<User, Double> userCompletionsPercentages = new HashMap<User, Double>();
        for(User user : this.users){
            Double complitions = user.getComplitionOfDataset(this);
            userCompletionsPercentages.put(user, complitions);
        }
        return userCompletionsPercentages;
    }

    protected HashMap<User, Double> getUserConsistencyPercentages(){
        HashMap<User, Double> userConsistencyPercentages = new HashMap<User, Double>();
        for(User user : this.users){
            Double consistency = user.getConsistencyPercentageOfDataset(this);
            userConsistencyPercentages.put(user, consistency);
        }
        return userConsistencyPercentages;
    }

    protected HashMap<Label, Double> getFinalLabelPercentages(){
        HashMap<Label, Integer> finalLabelCounts = new HashMap<Label, Integer>();
        HashMap<Label, Double> finalLabelPercentages = new HashMap<Label, Double>();
        Integer totalNumberOfInstances = 0;
        for(Instance instance : this.instances){
            if(!instance.getAssignments().isEmpty()){
                Label finalLabel = instance.getFinalLabel();
                if(!finalLabelCounts.containsKey(finalLabel)){
                    finalLabelCounts.put(finalLabel, 1);
                }else{
                    finalLabelCounts.replace(finalLabel, finalLabelCounts.get(finalLabel)+1);
                }
                totalNumberOfInstances ++;
            }
        }
        for (Map.Entry<Label, Integer> entry : finalLabelCounts.entrySet()) {
            Double percentage = entry.getValue().doubleValue() / totalNumberOfInstances.doubleValue() * 100.0;
            finalLabelPercentages.put(entry.getKey(), percentage);
        }
        return finalLabelPercentages;
    }

    protected HashMap<Label, ArrayList<Instance>> getLabelInstanceList(){
        HashMap<Label, ArrayList<Instance>> labelInstanceList = new HashMap<Label, ArrayList<Instance>>();
        for(Instance instance : this.instances){
            for(Label label : instance.getUniqueLabels()){
                if(labelInstanceList.containsKey(label)){
                    labelInstanceList.get(label).add(instance);
                }else{
                    ArrayList<Instance> instances = new ArrayList<Instance>();
                    instances.add(instance);
                    labelInstanceList.put(label, instances);
                }
            }
        }
        return labelInstanceList;
    }

    /*
     * Creates a <Instance> object with its id and text then adds <Instance> object to instances list of dataset.
     * @param   id                      id of instance
     * @param   text                    text of instance
     * @return                          created <Instance> object
     */
    protected Instance addInstance(int id, String text){
        Instance newInstance = new Instance(id, text, this);
        this.instances.add(newInstance);
        return newInstance;
    }

    /*
     * Creates a <Label> object with its id and text then adds <Label> object to labels list of dataset.
     * This function is used during the JSON reading process.
     * @param   id                      id of label
     * @param   text                    text of label
     * @return                          created <Label> object
     */
    protected Label addLabel(int id, String text){
        Label label = new Label(id, text);
        this.labels.add(label);
        return label;
    }
    
    protected void addUser(User user){
        if(!this.users.contains(user)){
            this.users.add(user);
        }
    }
    /*
     * Prints detailed dataset parameters.
     * This method is useful for development purposes.
     * @return                          nothing
     */
    protected void printDatasetDetailed(){
        for(Instance instance : this.instances){
            System.out.println("ID: "+instance.getId() + " Text:" + instance.getText());
            for(Assignment assignment : instance.getAssignments()){
                System.out.println("---Instance ID: "+assignment.getInstance().getId()+" Username: "+assignment.getUser().getName());
                for(Label label : assignment.getLabels()){
                    System.out.println("------Label: "+label.getText());
                }
            }
        }
    }

    protected void printUserCompletions(){
        HashMap<User, Double> userCompletions = this.getUserCompletionsPercentages();
        for (Map.Entry<User, Double> entry : userCompletions.entrySet())
        {
            Double percentage = entry.getValue()*100;
            System.out.println(entry.getKey().getName()+" : "+percentage.intValue()+" %");
        }
    }
    
    protected void printUserConsistencies(){
        HashMap<User, Double> userConsistencyPercentages = this.getUserConsistencyPercentages();
        for (Map.Entry<User, Double> entry : userConsistencyPercentages.entrySet())
        {
            Double percentage = entry.getValue()*100;
            System.out.println(entry.getKey().getName()+" : "+percentage.intValue()+" %");
        }
    }

    protected void printLabelFrequencies(){
        HashMap<Label, Double> labelFrequencies = getLabelFrequencies();
        for(Map.Entry<Label, Double> entry : labelFrequencies.entrySet()){
            System.out.println(entry.getKey().getText()+" => "+entry.getValue());
        }
    }
    
    protected void printUserList(){
        for(User user : this.users){
            System.out.println(user.getName());
        }
    }

    protected void printFinalLabelPercentages(){
        HashMap<Label, Double> finalLabelPercentages = getFinalLabelPercentages();
        for(Map.Entry<Label, Double> entry : finalLabelPercentages.entrySet()){
            System.out.println(entry.getKey().getText()+" => "+entry.getValue());
        }
    }

    protected void printLabelInstanceList(){
        HashMap<Label, ArrayList<Instance>> labelInstanceList = getLabelInstanceList();
        for(Map.Entry<Label, ArrayList<Instance>> entry : labelInstanceList.entrySet()){
            String row = entry.getKey().getText()+" => ";
            for(Instance instance : entry.getValue()){
                row = row + instance.getId() + ",";
            }
            System.out.println(row);
        }
    }

    protected void printPerformanceMetrics(){
        System.out.println("\u001B[34m"+"DATASET PERFORMANCE METRICS"+"\u001B[0m");
        System.out.println("1. Completion Percentages:");
        System.out.println(getCompletionPercentage());
        System.out.println("2. Final Label Percentage:");
        printFinalLabelPercentages();
        System.out.println("3. List of Unique Instances With Related Labels:");
        printLabelInstanceList(); //@todo check if unique
        System.out.println("4. Number of User Assigned");
        System.out.println(getNumberOfUsers());
        System.out.println("5. List of Users Assigned and Their Completeness Percentages");
        printUserCompletions();
        System.out.println("5. List of Users Assigned and Their Consistency Percentages");
        printUserConsistencies();
    }

}