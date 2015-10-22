package info.smartkit.spring_data_solr.domain;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class Book {

	@Field
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Field
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Field
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Field("categories_txt")
	private List<Category> categories;

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	
	@Override
	public String toString() {
		return "Book [id=" + id + ", description=" + description + ", title=" + name + "]";
	}

}
