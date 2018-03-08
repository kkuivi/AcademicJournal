import java.util.*;
    
public class AcademicJournal {

	public class Journal  implements Comparable<Journal> {
		public String name;
		public float totalCountOfCitations;
		public float numDocuments;
		public float average;
		
		
		public Journal(String name, float totalCountOfCitations, float numDocuments, float average) {
			this.name = name;
			this.totalCountOfCitations = totalCountOfCitations;
			this.numDocuments = numDocuments;
			this.average = average;
		}

		public void calculateAvg() {
			if(numDocuments > 0) {
				this.average = this.totalCountOfCitations/this.numDocuments;
			}
		}

		public int compareTo(Journal otherJournal) {

			if(this.average < otherJournal.average) {
				return 1;
			}
			else if (this.average > otherJournal.average) {
				return -1;
			}
			else {
				if (this.numDocuments < otherJournal.numDocuments) {
					return 1;
				}
				else if(this.numDocuments > otherJournal.numDocuments) {
					return -1;
				}
				else {
					return this.name.compareTo(otherJournal.name);
				}
			}
		}
	}


	public class Document {
		public String journalName;
		public HashSet<String> documentsCited;
		public int index;

		public Document(String journalName, HashSet<String> documentsCited, int index) {
			this.journalName = journalName;
			this.documentsCited = documentsCited;
			this.index = index;
		}
	}


	public String[] rankByImpact(String[] input) {

		HashSet<String> exploredCitations = new HashSet<>();
		HashMap<String, Journal> journalMap = new HashMap<>();
		HashMap<String, Document> docMap = new HashMap<>();

		buildJournalAndDocMaps(input, journalMap, docMap);
		recordCitations(exploredCitations, journalMap, docMap);	

		ArrayList<Journal> journals = new ArrayList<>();
		for (String journalName : journalMap.keySet()) {
			journals.add(journalMap.get(journalName));
		}

		Collections.sort(journals);

		String[] res = new String[journals.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = journals.get(i).name;
		}

		return res;

	}

	public void buildJournalAndDocMaps(String[] input, HashMap<String, Journal> journalMap, HashMap<String, Document> docMap) {
		for(int i = 0; i < input.length; i++) {
			String s = input[i];
			int dotIndex = s.indexOf('.');

			if(dotIndex > -1) {
				String journalName = s.substring(0,dotIndex);
				Document doc = new Document(journalName, new HashSet<>(), i);

                if(dotIndex+2 < s.length()) {
					String docCitedStr = s.substring(dotIndex+2,s.length());
                    String[] docCitedArr = docCitedStr.split(" ");
					
                    if(docCitedArr != null && docCitedArr.length > 0) {
						for (String c : docCitedArr) {
							doc.documentsCited.add(c);
						}
					}
				}

				if(!journalMap.containsKey(journalName)) {
					Journal journal = new Journal(journalName, 0, 1, 0);
					journalMap.put(journalName, journal);
				}
				else {
					journalMap.get(journalName).numDocuments += 1;
				}

				if(!docMap.containsKey(i)) {
					docMap.put(String.valueOf(i), doc);
				}
			}
		}
	}

	public void recordCitations(HashSet<String> exploredCitations, HashMap<String, Journal> journalMap, HashMap<String, Document> docMap) {

		for (String docIndex : docMap.keySet()) {
			Document doc = docMap.get(docIndex);

			for(String citedDocIndex : doc.documentsCited) {		

				if(docMap.containsKey(citedDocIndex)) {
					String citedDocJournalName = docMap.get(citedDocIndex).journalName;

					if(journalMap.containsKey(citedDocJournalName)) {
						Journal journal = journalMap.get(citedDocJournalName);

						String connection = docIndex + "-->" + citedDocIndex;
						boolean sameJournal = doc.journalName.equals(citedDocJournalName);
						if(!exploredCitations.contains(connection) && !sameJournal) {
							journal.totalCountOfCitations += 1;
							journal.calculateAvg();
							exploredCitations.add(connection);
						}
					}			
				}
				
			}

		}


	}
