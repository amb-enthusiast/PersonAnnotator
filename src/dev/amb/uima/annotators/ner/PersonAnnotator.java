package dev.amb.uima.annotators.ner;

import java.io.IOException;
import java.net.URL;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import dev.amb.uima.typeSystem.core.Token;
import dev.amb.uima.typeSystem.ner.Person;
import dev.amb.uima.typeSystems.core.Sentence;



public class PersonAnnotator extends JCasAnnotator_ImplBase {

	// set up openNLP models
	private SentenceDetectorME sentDetector = null;
	private Tokenizer tokenizer = null;
	private NameFinderME personFinder = null;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {

		// create models
		try {
			// TODO replace with a ResourceManager mechanism to declare
			// dependencies and then load into context
			URL sentenceModelUrl = this.getClass().getResource(
					"models/en-sent.bin");
			
			URL tokenModelUrl = this.getClass().getResource(
					"models/en-token.bin");
			
			URL personModelUrl = this.getClass().getResource(
					"models/en-ner-person.bin");
			

			SentenceModel sentenceModel = new SentenceModel(sentenceModelUrl.openStream());
			this.sentDetector = new SentenceDetectorME(sentenceModel);

			TokenizerModel tokenModel = new TokenizerModel(tokenModelUrl.openStream());
			this.tokenizer = new TokenizerME(tokenModel);
			
			TokenNameFinderModel personModel = new TokenNameFinderModel(personModelUrl.openStream());
			this.personFinder = new NameFinderME(personModel);
			

		} catch (IOException ioe) {
			//
		}
		super.initialize(aContext);
	}

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		// get sentences, index annotations
		
		String text = jcas.getDocumentText();
		
		if (sentDetector != null) {
			Span[] sentences = sentDetector.sentPosDetect(text);
			
			for (Span sent : sentences) {
				
				
				// change comments to add sentences to CAS index
//				Sentence sentence = new Sentence(jcas, sent.getStart(), sent.getEnd());
//				sentence.addToIndexes();
				
				// then for each sentence annotation, get the tokens, and index
				if(tokenizer != null) {
					String sentenceValue = jcas.getDocumentText().substring(sent.getStart() , sent.getEnd());
					
					Span[] tokens = tokenizer.tokenizePos(sentenceValue);
					String[] tokenValues = tokenizer.tokenize(sentenceValue);
					
					
					// change comments to add/remove Token from CAS index
//					for(Span tokenSpan : tokens) {
//						// the tokens are measures from the start of the sentence string;
//						// shift by sentence length to source text get start and end indexes
//						
//						int tokenStart = sent.getStart() + tokenSpan.getStart();
//						int tokenEnd = sent.getStart() + tokenSpan.getEnd();
//						
//						Token token = new Token(jcas);
//						token.setBegin(tokenStart);
//						token.setEnd(tokenEnd);
//						
//						// add to all CAS indexes
//						token.addToIndexes();
//					}
					
					// use token spans and values to reference source text indexes
					
					Span[] people = personFinder.find(tokenValues);
					
					// span gives start and end index in tokens[]
					// indexes match in tokenSpan and tokenValue, so for each token
					for(Span personSpan : people) {
					Span startTokenSpan = tokens[personSpan.getStart()];
					Span endTokenSpan = tokens[personSpan.getEnd() - 1];
					
					int tokenStart = sent.getStart() + startTokenSpan.getStart();
					int tokenEnd = sent.getStart() + endTokenSpan.getEnd();
					
					Person person = new Person(jcas);
					person.setBegin(tokenStart);
					person.setEnd(tokenEnd);
					person.addToIndexes();
					
					}
					
					
				}
			}
			
		}

	}

}
