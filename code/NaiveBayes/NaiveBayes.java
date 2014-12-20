import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.PrintWriter;

/**
 * Created by uditmehrotra on 23/11/14.
 */
public class NaiveBayes {

    private static int[] categorical = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
    private static int[] numeric = {21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42};

    private static ArrayList<Integer> training_labels;
    private static ArrayList<Integer> test_labels;
    private static ArrayList<HashMap<Integer,Integer>> training_data;
    private static ArrayList<HashMap<Integer,Integer>> test_data;
    private static HashMap<Integer, HashMap<Integer,HashMap<Integer,Double>>> classifier;
    private static HashMap<Integer,Integer> training_labels_frequency;
    private static HashMap<Integer,HashMap<Integer,Double>> attributes_mean;
    private static HashMap<Integer,HashMap<Integer,Double>> attributes_standard_deviation;
    private static ArrayList<Integer> predicted_labels;
    private static HashMap<Integer, ArrayList<Integer>> attribute_unique_values;

    public static void readTrainingData(String training_file)
    {
       training_labels = new ArrayList<Integer>();
       training_data = new ArrayList<HashMap<Integer, Integer>>();
       try
       {
           BufferedReader br = new BufferedReader(new FileReader(training_file));
           String line;

           //Reading first line
           line = br.readLine();
           String[] attributes = line.split(",");

           while ((line = br.readLine()) != null) {

               String[] values = line.split(",");
               int class_label = Integer.parseInt(values[0]);
               training_labels.add(class_label);

               HashMap<Integer,Integer> tuple = new HashMap<Integer, Integer>();
               for(int loop = 1; loop < values.length; loop++)
               {
                   tuple.put(loop, (int) Double.parseDouble(values[loop]));
               }

               training_data.add(tuple);
           }

           System.out.println(training_labels.size());
           System.out.println(training_data.size());
       }
       catch(Exception ex)
       {
           System.out.println(ex);
       }
    }

    public static void readTestData(String test_file)
    {
        //test_labels = new ArrayList<Integer>();
        test_data = new ArrayList<HashMap<Integer, Integer>>();
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(test_file));
            String line;

            //Reading first line
            line = br.readLine();
            String[] attributes = line.split(",");

            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");
                //int class_label = Integer.parseInt(values[0]);
                //test_labels.add(class_label);

                HashMap<Integer,Integer> tuple = new HashMap<Integer, Integer>();
                for(int loop = 0; loop < values.length; loop++)
                {
                    tuple.put(loop+1,(int) Double.parseDouble(values[loop]));
                }

                test_data.add(tuple);
            }

            //System.out.println("Test : " + test_labels.size());
            System.out.println("Test : " + test_data.size());
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }

    private static void get_means()
    {
        attributes_mean = new HashMap<Integer, HashMap<Integer,Double>>();
        for(int i=0; i < training_data.size(); i++)
        {
            //for(Integer label :  training_labels_frequency.keySet())
            //{
            int label = training_labels.get(i);

            for (int j = 0; j < numeric.length; j++)
                {
                    if (!attributes_mean.containsKey(numeric[j]))
                    {
                        HashMap<Integer,Double> temp = new HashMap<Integer, Double>();
                        temp.put(label, (double) training_data.get(i).get(numeric[j]));
                        attributes_mean.put(numeric[j], temp);
                    }
                    else
                    {
                        if(!attributes_mean.get(numeric[j]).containsKey(label))
                        {
                            attributes_mean.get(numeric[j]).put(label, (double) training_data.get(i).get(numeric[j]));
                        }
                        else
                        {
                            double value = attributes_mean.get(numeric[j]).get(label);
                            value = value + (double) training_data.get(i).get(numeric[j]);
                            attributes_mean.get(numeric[j]).put(label, value);
                        }
                    }
                }
            //}
        }

        for(Integer label :  training_labels_frequency.keySet())
        {
            for (Integer attr : attributes_mean.keySet())
            {
                double sum = attributes_mean.get(attr).get(label);
                sum = sum / (training_labels_frequency.get(label) * 1.0);
                attributes_mean.get(attr).put(label, sum);
            }
        }

        System.out.println(attributes_mean.size());
    }

    private static void get_standard_deviations()
    {

        attributes_standard_deviation = new HashMap<Integer, HashMap<Integer,Double>>();
        for(int i=0; i < training_data.size(); i++)
        {
            int label = training_labels.get(i);
            for (Integer attr : numeric)
            {
                double mean = attributes_mean.get(attr).get(label);
                if(!attributes_standard_deviation.containsKey(attr))
                {
                    double val = training_data.get(i).get(attr);
                    val = Math.pow((val - mean),2);
                    HashMap<Integer,Double> temp = new HashMap<Integer, Double>();
                    temp.put(label,val);
                    attributes_standard_deviation.put(attr,temp);
                }
                else
                {
                    if(!attributes_standard_deviation.get(attr).containsKey(label))
                    {
                        double val = training_data.get(i).get(attr);
                        val = Math.pow((val - mean),2);
                        attributes_standard_deviation.get(attr).put(label,val);
                    }
                    else
                    {
                        double val = attributes_standard_deviation.get(attr).get(label);
                        double val1 = training_data.get(i).get(attr);
                        val1 = Math.pow((val1-mean),2);
                        val = val + val1;
                        attributes_standard_deviation.get(attr).put(label, val);
                    }

                }
            }
        }

        for(Integer label :  training_labels_frequency.keySet())
        {
            for (Integer attr : attributes_standard_deviation.keySet())
            {
                double val = attributes_standard_deviation.get(attr).get(label);
                val = val / training_labels_frequency.get(label);
                val = Math.sqrt(val);
                attributes_standard_deviation.get(attr).put(label, val);
            }
        }

        System.out.println(attributes_standard_deviation.size());
    }

    private static void build_classifier()
    {
        classifier = new HashMap<Integer,HashMap<Integer, HashMap<Integer, Double>>>();
        training_labels_frequency = new HashMap<Integer, Integer>();
        for(int i=0; i < training_data.size(); i++)
        {
            //Count the frequency of occurrence of each class
            int label = training_labels.get(i);
            if (training_labels_frequency.containsKey(label)) {
                int val = training_labels_frequency.get(label);
                training_labels_frequency.put(label, ++val);
            } else {
                training_labels_frequency.put(label, 1);
            }

            for(Integer attr : categorical)
            {
                int val = training_data.get(i).get(attr);
                if (!classifier.containsKey(attr)) {
                    HashMap<Integer, HashMap<Integer, Double>> attr_values = new HashMap<Integer, HashMap<Integer, Double>>();
                    HashMap<Integer, Double> values_class_frequency = new HashMap<Integer, Double>();
                    values_class_frequency.put(label, 1.0);
                    attr_values.put(val, values_class_frequency);
                    classifier.put(attr, attr_values);
                } else {
                    if (!classifier.get(attr).containsKey(val)) {
                        HashMap<Integer, Double> values_class_frequency = new HashMap<Integer, Double>();
                        values_class_frequency.put(label, 1.0);
                        classifier.get(attr).put(val, values_class_frequency);
                    } else {
                        if (!classifier.get(attr).get(val).containsKey(label)) {
                            classifier.get(attr).get(val).put(label, 1.0);
                        } else {
                            double count = classifier.get(attr).get(val).get(label);
                            classifier.get(attr).get(val).put(label, ++count);
                        }
                    }
                }
            }
        }


        //adding labels which might not have occurred for a particular attribute-value pair
        for(Integer attribute : classifier.keySet())
        {
            for(Integer value : classifier.get(attribute).keySet())
            {
                for(Integer class_label : training_labels_frequency.keySet())
                {
                    if(!classifier.get(attribute).get(value).containsKey(class_label)) {
                        classifier.get(attribute).get(value).put(class_label, 0.0);
                    }
                }
            }
        }

        System.out.println(training_labels_frequency.size());
        System.out.println(classifier.size());
    }

    private static boolean isCategorical(int attribute)
    {
        boolean iscategorical = false;
        for(int i = 0; i < categorical.length; i++)
        {
            if(categorical[i] == attribute)
            {
                iscategorical = true;
                break;
            }
        }

        return iscategorical;
    }

    private static void predict(ArrayList<HashMap<Integer,Integer>> data, boolean applyLaplace)
    {
        predicted_labels = new ArrayList<Integer>();

        for(int i=0; i < data.size(); i++)
        {
            double max_probability = 0.0;
            int predicted_label = 0;
            //System.out.println(i);
            for (Integer class_label : training_labels_frequency.keySet())
            {
                double conditional_probability = 1.0;
                int class_frequency = training_labels_frequency.get(class_label);
                for (Integer attr : data.get(i).keySet())
                {
                    int value = data.get(i).get(attr);

                    if(isCategorical(attr))
                    {
                        double attr_value_class_frequency;
                        if (classifier.get(attr).containsKey(value))
                        {
                            attr_value_class_frequency = classifier.get(attr).get(value).get(class_label);
                            if(applyLaplace)
                            {
                                attr_value_class_frequency++;
                            }
                        }
                        else
                        {
                            //attr_value_class_frequency = 1.0;
                            if(applyLaplace) {
                                attr_value_class_frequency = 1.0;
                            }
                            else
                                attr_value_class_frequency = 0.0;
                        }

                        //conditional_probability = conditional_probability * (attr_value_class_frequency / (class_frequency * 1.0));
                        if(!applyLaplace)
                        {
                            conditional_probability = conditional_probability * (attr_value_class_frequency / (class_frequency * 1.0));
                        }
                        else {
                            int freq = class_frequency + attribute_unique_values.get(attr).size();
                            conditional_probability = conditional_probability * (attr_value_class_frequency / (freq * 1.0));
                        }
                    }
                    else
                    {
                        double mean = attributes_mean.get(attr).get(class_label);
                        double standard_deviation = attributes_standard_deviation.get(attr).get(class_label);
                        double pi = Math.PI;
                        double e = Math.E;
                        double prob = Math.sqrt(2 * pi);
                        prob = 1 / (prob * standard_deviation * 1.0);

                        double val1 = Math.pow(value - mean, 2) / (2 * Math.pow(standard_deviation,2) * 1.0);
                        val1 = Math.pow(e,val1);
                        prob = prob / val1;

                        conditional_probability = conditional_probability * prob;
                    }
                }

                double current_probability = conditional_probability * (training_labels_frequency.get(class_label) / (training_data.size() * 1.0));
                if (max_probability < current_probability) {
                    max_probability = current_probability;
                    predicted_label = class_label;
                }
            }

            predicted_labels.add(predicted_label);
        }

    }

    public static void generate_rule_measures(ArrayList<Integer> labels, ArrayList<Integer> predicted)
    {
        int true_positive = 0;
        int true_negative = 0;
        int false_positive = 0;
        int false_negative = 0;
        for(int i = 0; i < labels.size(); i++)
        {
            int actual_label = labels.get(i);
            int predicted_label = predicted_labels.get(i);
            if(actual_label == 1 && predicted_label == 1)
                true_positive++;
            else if(actual_label == 1 && predicted_label == 0 )
                false_negative++;
            else if(actual_label == 0 && predicted_label == 1)
                false_positive++;
            else if(actual_label == 0 && predicted_label == 0)
                true_negative++;
        }
        System.out.println(true_positive + " " + false_negative + " " + false_positive + " " + true_negative);
        double accuracy = (true_positive + true_negative) / ((false_positive + false_negative + true_positive + true_negative) * 1.0);
        System.out.println("Accuracy : " + accuracy);
    }

    private static void get_attribute_unique_values(ArrayList<HashMap<Integer,Integer>> data1, ArrayList<HashMap<Integer,Integer>> data2)
    {
        attribute_unique_values = new HashMap<Integer, ArrayList<Integer>>();

        for(int i = 0; i < data1.size(); i++)
        {
            for(Integer attr : data1.get(i).keySet())
            {
                if(isCategorical(attr))
                {
                    if (!attribute_unique_values.containsKey(attr)) {
                        ArrayList<Integer> values = new ArrayList<Integer>();
                        values.add(data1.get(i).get(attr));
                        attribute_unique_values.put(attr, values);
                    } else {
                        int value = data1.get(i).get(attr);
                        if (!attribute_unique_values.get(attr).contains(value))
                            attribute_unique_values.get(attr).add(value);
                    }
                }
            }
        }

        for(int i = 0; i < data2.size(); i++)
        {
            for(Integer attr : data2.get(i).keySet())
            {
                if(isCategorical(attr)) {
                    if (!attribute_unique_values.containsKey(attr)) {
                        ArrayList<Integer> values = new ArrayList<Integer>();
                        values.add(data2.get(i).get(attr));
                        attribute_unique_values.put(attr, values);
                    } else {
                        int value = data2.get(i).get(attr);
                        if (!attribute_unique_values.get(attr).contains(value))
                            attribute_unique_values.get(attr).add(value);
                    }
                }
            }
        }
    }



    public static void main(String args[])
    {

        System.out.println("Data Mining Project - Naive Bayes");
        String training_file = args[0];
        String test_file = args[1];
        //Reading Training Data
        readTrainingData(training_file);

        //Reading Test Data
        readTestData(test_file);

        //Build Classifier
        build_classifier();

        //Get Attribute Means
        get_means();

        //Get Attribute Standard Deviations
        get_standard_deviations();

        //Get Attribute Unique Values
        get_attribute_unique_values(training_data,test_data);

        //Predict on training data
        predict(training_data,true);

        //Generate Rule Measures
        generate_rule_measures(training_labels,predicted_labels);

        //Predict on test data
        predict(test_data,true);
        System.out.println(predicted_labels.size());

        System.out.println(predicted_labels.size());

        try
        {
            PrintWriter pw = new PrintWriter("output.txt");
            for(int i = 0; i < predicted_labels.size(); i++)
            {
                pw.println(predicted_labels.get(i));
            }
            pw.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }

    }
}
