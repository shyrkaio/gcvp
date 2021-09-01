/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.kanedafromparis.shyrka;

/**
 * <p>ShyrkaLabel class.</p>
 *
 * @author csabourdin
 * @version $Id: $Id
 */
public enum ShyrkaLabel {
    //this is the stack name 
    L_PROJECT_NAME("io.shyrka.erebus/pjt-name"),
    //this is the stack name 
    L_STACK_NAME("io.shyrka.erebus/stack"),
    //this is the stage with this object belong to
    // dev, pprod, prod    
    L_PROJECT_STAGE("io.shyrka.erebus/pjt-stage"),
    //this is the stack version
    L_STACK_VERSION("io.shyrka.erebus/$(stack)-version"),
        //this is supposed date of the project, 
    //after that date the project is autmatically scaled down
    // default : Now() -2 days
    // Format ISO_8601_EXTENDED_DATE_FORMAT
    L_END_DATE("io.shyrka.erebus/end-date"),
    //this is supposed date of the project, 
    //after that date the project is autmatically scaled down
    // default : Now() -2 days
    // Format ISO_8601_EXTENDED_DATE_FORMAT
    
    A_DATA_CLASSIFICATION_LEVEL("io.shyrka.erebus/data-classification-level"),
    //email for personne to be alert of actions on project
    //Also to check that he exist
    // @ can not be store in label so 
    L_PRODUCT_OWNER("io.shyrka.erebus/product.owner"),
    A_PRODUCT_OWNER("io.shyrka.erebus/product.owner.email"),
    //email list for personne to be warn of actions on project
    A_TEAM_WATCHERS("io.shyrka.erebus/team.watchers"),
    //Last validation of the team owner
    //
    L_PRODUCT_OWNER_LAST_ACKNOWLEDGEMENT("io.shyrka.erebus/product.owners.last.acknowledgement"),
    
    //this is weither of not to scaledown the project
    // default true
    L_SCALEDOWN("io.shyrka.gcvp/scaledown"),
    //this is weither of not to scaledown the project
    // default false
    L_RESTART("io.shyrka.gcvp/auto-start"),
    //this is weither of not to backup the project
    // default false    
    L_BACKUP("io.shyrka.gcvp/backup"),
    A_BACKUP_LAST("io.shyrka.gcvp/backup.last");
    
    // Cheers API
    private final String label;

    ShyrkaLabel(String label) {
        this.label = label;
    }

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getlabel() {
        return this.label;
    }

    /**
     * <p>build.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String build(String s) {
        return this.label + s;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.label;
    }
    
}
