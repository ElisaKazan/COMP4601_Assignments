package edu.carleton.COMP4601.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.mongodb.client.model.FindOneAndReplaceOptions;

import edu.carleton.COMP4601.db.Database;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentCollection {
	private static DocumentCollection collection;
	@XmlElement(name="documents")
	private List<Document> documents;
	private static String DOC_COLLECTION_NAME = "documents";
	
	public interface DocumentPredicate {
		boolean matches(Document d);
	}
	
	public DocumentCollection() {
		loadAll();
	}
	
	public void loadAll() {
		for (org.bson.Document d : Database.getDB().getCollection(DOC_COLLECTION_NAME).find()) {
			documents.add(new Document(d));
		}
	}
	
	public void saveAll() {
		for (Document d : documents) {
			saveOne(d);
		}
	}
	
	public Document findOne(DocumentPredicate predicate) {
		List<Document> docs = findAll(predicate);
		
		if (docs.size() == 0) {
			return null;
		}
		
		return docs.get(0);
	}
	
	public Document findOne(int id) {
		return findOne(new DocumentPredicate() {
			
			@Override
			public boolean matches(Document d) {
				// TODO Auto-generated method stub
				return d.getId() == id;
			}
		});
	}
	
	public List<Document> findAll(DocumentPredicate predicate) {
		List<Document> docs = new ArrayList<Document>();
		for (Document d : docs) {
			if (predicate.matches(d)) {
				docs.add(d);
			}
		}
		
		return docs;
	}
	
	public void saveOne(Document d) {
		org.bson.Document converted = d.save();

		Database.getCollection(DOC_COLLECTION_NAME).findOneAndReplace(eq("_id", converted.get("_id")), converted, new FindOneAndReplaceOptions().upsert(true));
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public static DocumentCollection getInstance() {
		if (collection == null) {
			collection = new DocumentCollection();
		}
		
		return collection;
	}

	public boolean add(Document d) {
		if (documents.contains(d)) {
			return false;
		}
		
		documents.add(d);
		
		saveOne(d);
		
		return true;
	}
}