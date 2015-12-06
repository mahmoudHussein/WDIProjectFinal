package de.uni_mannheim.informatik.wdi.usecase.books;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.joda.time.DateTime;
import org.xml.sax.SAXException;

import de.uni_mannheim.informatik.wdi.DataSet;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.Blocker;
import de.uni_mannheim.informatik.wdi.identityresolution.blocking.PartitioningBlocker;
import de.uni_mannheim.informatik.wdi.identityresolution.evaluation.GoldStandard;
import de.uni_mannheim.informatik.wdi.identityresolution.evaluation.MatchingEvaluator;
import de.uni_mannheim.informatik.wdi.identityresolution.evaluation.Performance;
import de.uni_mannheim.informatik.wdi.identityresolution.matching.Correspondence;
import de.uni_mannheim.informatik.wdi.identityresolution.matching.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.wdi.identityresolution.matching.MatchingEngine;
import de.uni_mannheim.informatik.wdi.identityresolution.model.DefaultRecord;
import de.uni_mannheim.informatik.wdi.identityresolution.model.DefaultRecordCSVFormatter;
import de.uni_mannheim.informatik.wdi.usecase.books.comparators.BookPublicationDateComparator;
import de.uni_mannheim.informatik.wdi.usecase.books.comparators.BooksAuthorsComparator;
import de.uni_mannheim.informatik.wdi.usecase.books.comparators.BooksISBNComparator;
import de.uni_mannheim.informatik.wdi.usecase.books.comparators.BooksTitleJaccardComparator;
import de.uni_mannheim.informatik.wdi.usecase.books.comparators.BooksTitleLevenshteinComparator;


public class Books_Main {

	public static void main(String[] args) throws XPathExpressionException,
	ParserConfigurationException, SAXException, IOException {
		DateTime startTime=DateTime.now();
		
	
		
		// define the matching rule	
		LinearCombinationMatchingRule<Books> rule = new LinearCombinationMatchingRule<>(0.7);
//		rule.addComparator(new BooksTitleJaccardComparator(), 0.1);   			//we need to create the matching rules here for ISBN,
////		rule.addComparator(new BooksTitleLevenshteinComparator(), 0.1 );			//	Book_Name, Authors, Publisher
//		rule.addComparator(new BooksISBNComparator(), 0.7);
//		rule.addComparator(new BookPublicationDateComparator(),0.1);
		rule.addComparator(new BooksAuthorsComparator(), 1.0);
		
		// create the matching engine
		Blocker<Books> blocker = new PartitioningBlocker<>(new BooksBlockingFunctionAuthor());
		MatchingEngine<Books> engine = new MatchingEngine<>(rule, blocker);

		File dataset1 = new File("usecase/books/input/fused.xml");
		File dataset2 = new File("usecase/books/input/AuthorTargetSchemaB.xml");
		
	
		// load the data sets
				DataSet<Books> ds1 = new DataSet<>();
				DataSet<Books> ds2 = new DataSet<>();
				ds1.loadFromXML(
						dataset1,
						new BooksFactory(), "/Books/Book");
				ds2.loadFromXML(
						dataset2,
						new BooksFactory(), "/Books/Book");
				

				// run the matching
				List<Correspondence<Books>> correspondences = engine.runMatching(ds1, ds2);
	
				// write the correspondences to the output file

				engine.writeCorrespondences(correspondences, new File("usecase/books/output/fusedBooks_2_DbpediaAuthors_Correspondences.csv"));
	
				printCorrespondences(correspondences);
				
				// load the gold standard (training set)
				GoldStandard gsTraining = new GoldStandard();
				gsTraining.loadFromCSVFile(new File(
						"usecase/books/goldstandard/GS_FusedBooks_Authors_training.csv"));  //name of the gold standard (IMP) change the existing its for movies

				// create the data set for learning a matching rule (use this file in RapidMiner)
				DataSet<DefaultRecord> features = engine
						.generateTrainingDataForLearning(ds1, ds2, gsTraining);
				features.writeCSV(
						new File(
								"usecase/books/output/optimisation/FusedBooks_2_Authors_Features.csv"),
						new DefaultRecordCSVFormatter());
				
				// load the gold standard (test set)
				GoldStandard gsTest = new GoldStandard();
				gsTest.loadFromCSVFile(new File(
						"usecase/books/goldstandard/GS_FusedBooks_Authors_test.csv")); //name of gold standard (IMP) change the existing its for movies

				// evaluate the result
				MatchingEvaluator<Books> evaluator = new MatchingEvaluator<>(true);
				Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);
				
				// print the evaluation result
				System.out.println(String.format(
						"Precision: %.4f\nRecall: %.4f\nF1: %.4f", perfTest.getPrecision(),
						perfTest.getRecall(), perfTest.getF1()));
				DateTime endTime=DateTime.now();
				System.out.println("Start time: " + startTime);
				System.out.println("End time: " + endTime);
				
	
			
	
	
	}
	
	private static void printCorrespondences(List<Correspondence<Books>> correspondences) {
		// sort the correspondences
		Collections.sort(correspondences, new Comparator<Correspondence<Books>>() {

			@Override
			public int compare(Correspondence<Books> o1, Correspondence<Books> o2) {
				int score = Double.compare(o1.getSimilarityScore(), o2.getSimilarityScore());
				int title = o1.getFirstRecord().getBookName().compareTo(o2.getFirstRecord().getBookName());
				
				if(score!=0) {
					return -score;
				} else {
					return title;
				}
			}

		});
		
		// print the correspondences
		for (Correspondence<Books> correspondence : correspondences) {
			if(correspondence.getSimilarityScore()<1.0) {
				System.out.println(String.format("%s,%s,|\t\t%.2f\t[%s] %s (%s) <--> [%s] %s (%s)",
						correspondence.getFirstRecord().getIdentifier(),
						correspondence.getSecondRecord().getIdentifier(),
						correspondence.getSimilarityScore(),
						correspondence.getFirstRecord().getIdentifier(), correspondence
								.getFirstRecord().getBookName(), correspondence.getFirstRecord()
								.getYear(), correspondence
								.getSecondRecord().getIdentifier(), correspondence
								.getSecondRecord().getBookName(), correspondence.getSecondRecord()
								.getYear()));
			}
		}
	}
	
	
	
}
