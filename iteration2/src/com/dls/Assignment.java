package com.dls;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Assignment class stores assignments of an instance.
 * Instances may have more than one assignment which created by different users (bots).
 * Assignments should have at least one assigned label.
 * @version iteration-1.0
 * @since 2020-12-01
 *
 */
public class Assignment {

    private int instanceId;
    private Dataset dataset;
    private Instance instance;
    private User user;
    private Datetime datetime;

    private ArrayList<Label> labels = new ArrayList<Label>();

    /*
     * Construct method of the Assignment class
     * @param   instance                instance object to assign this object
     * @param   datetime                datatime object to
     * @param   maxNumberOfLabels       maximum number of labels to assign a single instance
     * @return  nothing
     */
    public Assignment(Instance instance, User user) {

        // @todo datetime should be created under construct method, not as an input.
        setInstance(instance);
        setDatetime(new Datetime());
        setUser(user);
        setDataset(instance.getDataset());

    }

    protected void setDataset(Dataset dataset){
        this.dataset = dataset;
    }

    /*
     * Sets instance object as instance variable
     * Sets instance ID as intance variable
     * @todo check if it is suitable
     * @param   instance                instance object to set instanceId and object
     * @return  nothing
     */
    protected void setInstance(Instance instance) {
        this.instance = instance;
        this.instanceId = instance.getId();
    }
    /*
     * Sets datatime object as instance variable
     * @param   datetime                datetime object to set
     * @return                          nothing
     */
    protected void setDatetime(Datetime datetime) {
        this.datetime = datetime;
    }
    /*
     * Sets user object as instance variable
     * @param   user                    user object to set
     * @return                          nothing
     */
    protected void setUser(User user) {
        this.user = user;
    }
    /*
     * Get id of instance object @todo check if it is suitable
     * @return                          id of instance object
     */
    protected int getInstanceId(){
        return this.instanceId;
    }
    /*
     * Get user object
     * @return                          user object
     */
    protected User getUser() {
        return this.user;
    }
    /*
     * Get datetime object
     * @return                          datetime object
     */
    protected Datetime getDatetime() {
        return this.datetime;
    }
    /*
     * Get array list of labels
     * @return                          array list of labels
     */
    protected ArrayList<Label> getLabels() {
        return this.labels;
    }
    /*
     * Get list of label IDs.
     * v2
     * @return                          array list of label IDs
     */
    protected List<Integer> getLabelIDs(){
        List<Integer> labelWithIDs = new ArrayList<Integer>();
        for(Label label : this.labels){
            labelWithIDs.add(label.getId());
        }
        //Arrays.sort(labelsWithIDs);
        return labelWithIDs;
    }
    /*
     * Add assigned label to array list label.
     * @todo If maximum number of labels is reached, throw exception!
     * @param   label                   label to assign.
     * @return                          nothing
     */
    protected void addLabel(Label label) {
        if(this.labels.size() < this.instance.getDataset().getMaxNumberOfLabels()){
            this.labels.add(label);
            this.instance.getTheMostFrequentLabel();
        }
    }
    /*
     * Add label by label id. This method checks all label with given id
     * @param   id                      id of label to add labels list
     * @return                          nothing
     */
    protected void addLabelById(int id) {
        Label label = this.instance.getDataset().getLabelById(id);
        addLabel(label);
    }
    /*
     * Add label by label text. This method checks all label with given text
     * @param   text                    text of label to add labels list
     * @return                          nothing
     */
    protected void addLabelByText(String text) {
        Label label = this.instance.getDataset().getLabelByText(text);
        addLabel(label);
    }
    /*
     * Removes all labels that assigned.
     * @return                          nothing
     */
    protected void removeAllLabels(){
        for(Label label : getLabels()){
            this.labels.remove(label);
        }
    }
    /*
     * Compares label lists of this assignment and given assignment.
     * @param   <Assignment>            assignment object to compare
     * @return                          Boolean
     */
    protected Boolean compareAssignmentLabels(Assignment assignment){
        List<Integer> assignmentA = this.getLabelIDs();
        List<Integer> assignmentB = assignment.getLabelIDs();
        Collections.sort(assignmentA);
        Collections.sort(assignmentB);
        return assignmentA.equals(assignmentB);
    }



}
