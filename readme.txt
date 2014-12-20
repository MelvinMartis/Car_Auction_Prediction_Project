Pre-processing:
	Code: project_preprocess.py
	Note: The above code is ran and tested on continum anaconda python
	Explanation:
	>>In both training and test files we replaced NULL values for numeric attributes with the mean value of that attribute.
    >>For categorical variables with values like NULL, Not Available we replaced them with a new unique value in both training 	and test files.
 	>>We converted the unique text categorical values, to unique numeric labels by assigning a unique number to each value.
	>>For numeric attributes Gaussian distribution is used for handling numeric attributes to classify test data set.
	>>After careful analysis, we also created 4 new attributes based on existing MMR values as below:
		ProfitAcquisitionAverage=MMRAcquisitionRetailAveragePrice - MMRAcquisitionAuctionAveragePrice
		ProfitAcquisitionClean=MMRAcquisitonRetailCleanPrice - MMRAcquisitionAuctionCleanPrice
		ProfitCurrentClean=MMRCurrentRetailCleanPrice - MMRCurrentAuctionCleanPrice
		AverageProfit = ProfitAcquisitionAverage - ProfitCurrentAverage
		CleanProfit = ProfitAcquisitionClean - ProfitCurrentClean

	>>We also increased the weights of certain attributes, which enabled better accuracy with NB classification.

	Steps to run:
		Keep original training.csv and test.csv in the same folder as the above code, which can be found in pre-processing folder under code directory.
		run python project_preprocess.py
	Output: 
		x.csv - pre-processed training file
		y.csv - pre-processed test file
	Command: 
		sed -i 's/[^,]*,//' x.csv                                                                                                   
		sed -i 's/[^,]*,//' y.csv  
	We have used only above code for pre-processing and using output of the above code, specific pre-processing is carried out for individual classifiers.

K-Nearest Neighbor
--------------------
	Code:
		KNN.java
		ABC.java
	Pre-Processed files:
		Training file: x10.csv
		Test file: y10.csv
	Output: result.txt (Contains all the predicted class labels.)
	Kaggle File to upload: result_knn_new.csv
	Steps to run:
		Navigate to KNN folder, which is inside code folder.
		run javac *.java
		java KNN
		output: result.txt
		Copy all the values from output.txt and paste it against the RefId’s (from test data) in a csv file and then submit it to Kaggle.
		Note: Please ensure that both training and test file are present in the same folder as KNN.java file. Also the program takes approximately 3 hours to complete as there are ~73k records in training data and ~48K records in test data.

Naive Bayes Classifier
-----------------------
	Code:
		NaiveBayes.java
	Pre-Processed files:
		Training file: NaiveBayesTraining.csv
		Test file: NaiveBayesTest.csv
	Output: output.txt. (Contains all the predicted class labels.)
	Kaggle File to upload: output.csv
	Steps to run:
	1. Navigate to NaiveBayes folder inside code folder.
	2. run javac *.java
	3. java NaiveBayes NaiveBayesTraining.csv NaiveBayesTest.csv
	4. Check the output (predited labels): output.txt
	5. Copy all the values from output.txt and paste it against the RefId’s (from test data) in output.csv file and then submit it to Kaggle.
	Note: pass training and test file as first and second argument to NaiveBayes code.
		
