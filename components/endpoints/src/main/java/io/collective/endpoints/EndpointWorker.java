package io.collective.endpoints;

import io.collective.articles.ArticleDataGateway;
import io.collective.articles.ArticleInfo;
import io.collective.restsupport.RestTemplate;
import io.collective.rss.Item;
import io.collective.rss.RSS;
import io.collective.workflow.Worker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.collective.rss.Channel;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EndpointWorker implements Worker<EndpointTask> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RestTemplate template;
    private final ArticleDataGateway gateway;

    public EndpointWorker(RestTemplate template, ArticleDataGateway gateway) {
        this.template = template;
        this.gateway = gateway;
    }

    @NotNull
    @Override
    public String getName() {
        return "ready";
    }

    @Override
    public void execute(EndpointTask task) throws IOException {
        String response = template.get(task.getEndpoint(), task.getAccept());
        gateway.clear();

        XmlMapper mapper = new XmlMapper();
        RSS rss = mapper.readValue(response, RSS.class);
        List<Item> items = rss.getChannel().getItem();
        for (Item item:items){
            gateway.save(item.getTitle());
        }
    //     // Use XmlMapper to convert RSS response to a Java object
    //     RSS rss = new XmlMapper().readValue(response, RSS.class);

    //     // Extract relevant information from the RSS object and map to ArticleInfo instances
    //     // Extract relevant information from the RSS object and map to ArticleInfo instances
    //     List<ArticleInfo> articleInfos = new ArrayList<>();
    //     if (rss.getChannel() != null) {
    //         Channel channel = rss.getChannel();
            
    //         // Adjust this part based on your actual class structure
    //         List<Item> items = channel.getItem(); // Use getItem() instead of getItems()
    //         int i=0;
    //         if (items != null) {
    //             for (Item item : items) {
    //                 int uniqueIdentifier = i;
    //                 ArticleInfo articleInfo = new ArticleInfo(uniqueIdentifier, item.getTitle());
    //                 // You may extract and set other relevant information here
    //                 i+=1;
    //                 articleInfos.add(articleInfo);
    //             }
    //         }
    //     }

    
    //     for (ArticleInfo article : articleInfos) {
    //         String articleString = articleInfos.toString(); // Assuming ArticleInfo has a toString method
    //         gateway.save(articleString);
    //     }
    //    // gateway.save(articleInfos);
    //     // { // todo - map rss results to an article infos collection and save articles infos to the article gateway
    //     //     XmlMapper mapper = new XmlMapper();
    //     //     RSS rss = mapper.readValue(response);
    //     //     }
        
     }
}
