package qa.qcri.aidr.predict.data;

import java.net.InetAddress;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;

import org.json.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import qa.qcri.aidr.predict.classification.DocumentLabel;
import qa.qcri.aidr.predict.classification.DocumentLabelFilter;
import qa.qcri.aidr.predict.featureextraction.DocumentFeature;

/**
 * Document is an abstract representation of a work item in the processing
 * pipeline. Each process in the pipeline annotates the Document with additional
 * information such as features and class labels.
 * 
 * @author jrogstadius
 */
public abstract class Document implements java.io.Serializable {

    static final long serialVersionUID = 1L;
    public Long crisisID;
    public String crisisCode;
    public JSONObject inputJson;
    //public InetAddress sourceIP;
    public String language = "en";
    public Long documentID;
    
    public ArrayList<DocumentFeature> features = new ArrayList<DocumentFeature>();
    public ArrayList<DocumentLabel> labels = new ArrayList<DocumentLabel>();
    public int humanLabelCount = 0;
    public double valueAsTrainingSample = 0.5;

    public Document() {

    }

    public void setDocumentID(Integer documentID) {
        this.documentID = new Long(documentID);
    }
    
    public void setDocumentID(Long documentID) {
        this.documentID = documentID;
    }

    public Long getDocumentID() {
        return documentID;
    }

    public void setInputJson(JSONObject inputJson) {
        this.inputJson = inputJson;
    }

    public JSONObject getInputJson() {
        return inputJson;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setValueAsTrainingSample(double value) {
        this.valueAsTrainingSample = value;
    }

    public double getValueAsTrainingSample() {
        return valueAsTrainingSample;
    }

    public abstract String getDoctype();
    
    public abstract boolean isNovel();

    public void setCrisisID(int crisisID) {
        this.crisisID = new Long(crisisID);
    }
    
    public void setCrisisID(Long crisisID) {
        this.crisisID = crisisID;
    }

    public Long getCrisisID() {
        return crisisID;
    }

    public void setCrisisCode(String crisisCode) {
        this.crisisCode = crisisCode;
    }

    public String getCrisisCode() {
        return crisisCode;
    }

    /*
    @JsonIgnore
    public void setSourceIP(InetAddress source) {
        sourceIP = source;
    }
    
    @JsonIgnore
    public InetAddress getSourceIP() {
        return sourceIP;
    }
	*/
    
    public void addLabel(DocumentLabel label) {
        labels.add(label);

        if (label.isHumanLabel())
            humanLabelCount++;
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentLabel> ArrayList<T> getLabels(Class<T> classFilter) {
        ArrayList<T> items = new ArrayList<T>();

        for (DocumentLabel label : labels) {
            if (classFilter.isAssignableFrom(label.getClass()))
                items.add((T) label);
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentLabel> ArrayList<T> getHumanLabels(
            Class<T> classFilter) {
        ArrayList<T> items = new ArrayList<T>();

        for (DocumentLabel label : labels) {
            if (label.isHumanLabel()
                    && classFilter.isAssignableFrom(label.getClass()))
                items.add((T) label);
        }
        return items;
    }

    public boolean hasLabel(DocumentLabelFilter filter) {
        for (DocumentLabel label : labels) {
            if (filter.match(label))
                return true;
        }
        return false;
    }

    public boolean hasHumanLabels() {
        return humanLabelCount > 0;
    }

    @SuppressWarnings("unchecked")
    public <T extends DocumentFeature> ArrayList<T> getFeatures(
            Class<T> classFilter) {
        ArrayList<T> items = new ArrayList<T>();

        for (DocumentFeature feature : features) {
            if (classFilter.isAssignableFrom(feature.getClass()))
                items.add((T) feature);
        }
        return items;
    }

    public void addFeatureSet(DocumentFeature set) {
        features.add(set);
    }
}
