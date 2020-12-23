package com.dls;
import java.util.*;

/**
 * The Instance class stores variables and methods for each instance that created by WriteJSON class.
 * @version iteration-1.0
 * @since 2020-12-01
 * @todo Is status variable needed to show instance has assigned or not?
 *
 */
public class Instance {

    private int id;
    private String text;
    private boolean status = false; //
    private Dataset dataset;
    private Label finalLabel;

    private ArrayList<Assignment> assignments = new ArrayList<Assignment>(); // Make it limited
    private ArrayList<User> users = new ArrayList<User>();

    /*
     * Construct method of the Instance class
     * @param   id                  unique id number of instance
     * @param   text                text of the dataset
     * @param   <Dataset>           parent dataset object
     * @return  nothing
     */
    public Instance(int id, String text, Dataset dataset) {

        setId(id);
        setText(text);
        setDataset(dataset);

    }
    /*
     * Sets id of "instance" as instance variable
     * @todo Check if it used before
     * @param   id                      instance id
     * @return                          nothing
     */
    private void setId(int id) {
        this.id = id;
    }
    /*
     * Sets text of "instance" as instance variable
     * @param   text                    instance text
     * @return                          nothing
     */
    private void setText(String text) {
        this.text = text;
    }
    /*
     * Sets object of parent dataset as instance variable
     * @param   dataset                 parent dataset of instance
     * @return                          nothing
     */
    private void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }
    /*
     * Gets id of instance
     * @return                          instance id
     */
    protected int getId() {
        return this.id;
    }
    /*
     * Gets id of instance
     * @return                          instance text
     */
    protected String getText() {
        return this.text;
    }
    /*
     * Gets array list of instance object
     * @return                          array list of assignment object
     */
    protected ArrayList<Assignment> getAssignments() {
        return this.assignments;
    }
    /*
     * Gets object of parent dataset
     * @return                          parent dataset object
     */
    protected Dataset getDataset() {
        return this.dataset;
    }

    protected ArrayList<User> getUsers(){
        return this.users;
    }
    protected int getNumberOfAssignments(){
        return this.assignments.size();
    }

    protected int getNumberOfUniqueAssignments(){
        List<List<Integer>> uniqueLists = new ArrayList<List<Integer>>();
        int i = 0;
        for(Assignment assignment : this.assignments){
            List<Integer> labelIDs = assignment.getLabelIDs();  // TO CHECK
            Collections.sort(labelIDs);
            boolean unique = true;
            for (List<Integer> uniqueList : uniqueLists){
                Collections.sort(uniqueList);
                if (uniqueList.equals(labelIDs)){
                    unique = false;
                    break;
                }
            }
            if (unique){
                uniqueLists.add(labelIDs);
                i++;
            }
        }
        return uniqueLists.size();
    }

    protected int getNumberOfUsers(){
        return this.users.size();
    }

    protected ArrayList<Label> getUniqueLabels(){
        ArrayList<Label> labels = new ArrayList<Label>();
        for(Assignment assignment : this.assignments){
            for(Label label : assignment.getLabels()){
                if(!labels.contains(label)){
                    labels.add(label);
                }
            }
        }
        return labels;
    }

    protected HashMap<Label, Integer> getLabelFrequencies(){
        HashMap<Label, Integer> labelFrequencies = new HashMap<Label, Integer>();
        for(Assignment assignment : this.assignments){
            for(Label label : assignment.getLabels()){
                if(labelFrequencies.containsKey(label)){
                    labelFrequencies.replace(label, labelFrequencies.get(label)+1);
                }else{
                    labelFrequencies.put(label, 1);
                }
            }
        }
        return labelFrequencies;
    }

    protected HashMap<Label, Double> getLabelPercentages(){
        HashMap<Label, Integer> labelFrequencies = getLabelFrequencies();
        HashMap<Label, Double> labelPercentages = new HashMap<Label, Double>();
        Integer totalNumberOfLabels = 0;
        for(Map.Entry<Label, Integer> entry : labelFrequencies.entrySet()){
            totalNumberOfLabels += entry.getValue();
        }
        for (Map.Entry<Label, Integer> entry : labelFrequencies.entrySet()){
            labelPercentages.put(entry.getKey(), entry.getValue().doubleValue()/totalNumberOfLabels.doubleValue()*100.0);
        }
        return labelPercentages;
    }

    protected HashMap<Label, Double> getTheMostFrequentLabel(){
        HashMap<Label, Integer> labelFrequencies = this.getLabelFrequencies();
        HashMap<Label, Double> mostFrequent = new HashMap<Label, Double>();
        Map.Entry<Label, Integer> maxEntry = null;
        Integer totalNumberOfLabels = 0;
        for (Map.Entry<Label, Integer> entry : labelFrequencies.entrySet()){
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        for (Integer f : labelFrequencies.values()) { totalNumberOfLabels += f; }
        mostFrequent.put(maxEntry.getKey(), maxEntry.getValue().doubleValue()*100.0);
        this.finalLabel = maxEntry.getKey();
        return mostFrequent;
    }

    protected Label getFinalLabel(){
        return this.finalLabel;
    }

    protected Double getLabelDistributionEntropy(){
        HashMap<Label, Double> labelPercentages = getLabelPercentages();
        Double totalEntropy = 0.0;
        for (Map.Entry<Label, Double> entry : labelPercentages.entrySet()){
            Double percentage = entry.getValue().doubleValue() / 100.0;
            totalEntropy += -1.0 * percentage * Math.log(percentage) / Math.log(2);
        }
        return totalEntropy;
    }

    protected ArrayList<Assignment> getAssignmentsOfUser(User user){
        ArrayList<Assignment> assignmentsOfUser = new ArrayList<Assignment>();
        for (Assignment assignment : this.assignments){
            if(assignment.getUser().equals(user)){
                assignmentsOfUser.add(assignment);
            }
        }
        return assignmentsOfUser;
    }

    /*
     * Updates text of instance
     * @return                          nothing
     */
    protected void updateText(String text) {
        this.text = text;
    }
    /*
     * Creates new assignment objects and adds it to the array list of assignments.
     * @param   datetime                datetime object
     * @param   user                    user object
     * @return  <Assignment>            assignment object
     */
    protected Assignment addAssignment(User user) {

        Assignment assignment = new Assignment(this, user);
        this.assignments.add(assignment);
        user.addDataset(this.dataset);
        user.addAssignment(assignment);
        this.addUser(user);
        this.dataset.addUser(user);
        return assignment;
    }


    protected void addUser(User user){
        if(!this.users.contains(user)){
            this.users.add(user);
        }
    }

    protected boolean checkAssignmentConsistencyOfUser(User user){
        ArrayList<Assignment> assignmentsOfUser = getAssignmentsOfUser(user);
        for (int i = 0; i < assignmentsOfUser.size()-1; i++) {
            if(!assignmentsOfUser.get(i).compareAssignmentLabels(assignmentsOfUser.get(i+1))){
                return false;
            }
        }
        return true;
    }

    protected void printLabelPercentages(){
        HashMap<Label, Double> labelPercentages = getLabelPercentages();
        for (Map.Entry<Label, Double> entry : labelPercentages.entrySet()){
            System.out.println(entry.getKey().getText() + " => " + entry.getValue() + "%");
        }
    }

    protected void printMostFrequentLabel(){
        HashMap<Label, Double> mostFrequentLabel = getTheMostFrequentLabel();
        for (Map.Entry<Label, Double> entry : mostFrequentLabel.entrySet()){
            System.out.println(entry.getKey().getText() + " => " + entry.getValue() + "%");
        }
    }

    protected void printPerformanceMetrics(){
        System.out.println("\u001B[34m" + "INSTANCE PERFORMANCE METRICS" + "\u001B[0m");
        System.out.println("1. Number of Assignments");
        System.out.println(getAssignments().size());
        System.out.println("2. Number of Unique Assignments");
        System.out.println(getNumberOfUniqueAssignments());
        System.out.println("3. Number of Unique Users");
        System.out.println(getNumberOfUsers());
        System.out.println("4. Most Frequent Label and Its Percentage");
        this.printMostFrequentLabel();
        System.out.println("5. List of Labels and Their Percentages");
        this.printLabelPercentages();
        System.out.println("6. Entropy of the Labels");
        System.out.println(this.getLabelDistributionEntropy());
    }

}