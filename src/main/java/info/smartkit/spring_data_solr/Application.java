package info.smartkit.spring_data_solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@ComponentScan
//With this annotation we tell Spring Data Solr to look in the specified package for Solr repositories.
@EnableSolrRepositories("info.smartkit.spring_data_solr.repository")
//
public class Application {
	
	/*
	 * The solrServer bean is used to connect to the running Solr instance. 
	 */
	@Bean
	public SolrServer solrServer() {
		return new HttpSolrServer("http://localhost:8983/solr");
	}

	@Bean
	public SolrTemplate solrTemplate(SolrServer server) throws Exception {
		return new SolrTemplate(server);
	}
}
