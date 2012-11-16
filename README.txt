PersonAnnotator README

An Eclipse project defining an Apache UIMA Annotator (primitive Analysis Engine) which performs
Named Entitiy Recognition (NER) using the Apache OpenNP library to annotate person entities in documents.
NOTE: the default English language OpenNLP models are used.

The PersonAnnotator.java class contains commented out code which can be toggled to include sentence and word token annotations back into the CAS index.

A UIMA PEAR file PersonAnnotator.pear is also included, and can be loaded using the UIMA PEAR installer utility runPearInsaller.sh,
included in the $UIMA_HOME/bin directory.

The UIMA CAS Visual Debugger (documentAnalyzer.sh) can then be used to test out the annotator.
