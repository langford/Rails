package rails.game.specific._1880;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.Subgraph;

import rails.algorithms.NetworkEdge;
import rails.algorithms.NetworkGraphBuilder;
import rails.algorithms.NetworkVertex;
import rails.game.PublicCompanyI;
import rails.game.TokenI;

/**
 * This class stores and creates the various graphs
 * defined for each company
 *
 */
public class NetworkCompanyGraph_1880 {
    protected static Logger log =
        Logger.getLogger(NetworkCompanyGraph_1880.class.getPackage().getName());

    private final NetworkGraphBuilder graphBuilder;
    private final PublicCompanyI company;
        
    private NetworkCompanyGraph_1880(NetworkGraphBuilder graphBuilder, PublicCompanyI company) {
        this.graphBuilder = graphBuilder;
        this.company = company;
    }
    
    public static NetworkCompanyGraph_1880 create(NetworkGraphBuilder graphBuilder, PublicCompanyI company) {
       return new NetworkCompanyGraph_1880(graphBuilder, company);
    }
        
    public SimpleGraph<NetworkVertex, NetworkEdge> createConnectionGraph(boolean addHQ) {
        // get mapgraph from builder
        SimpleGraph<NetworkVertex, NetworkEdge> mapGraph = graphBuilder.getMapGraph();
        
        // set sinks on mapgraph
        NetworkVertex.initAllRailsVertices(mapGraph, company, null);
        
        // initialized simple graph
        SimpleGraph<NetworkVertex, NetworkEdge> graph = new SimpleGraph<NetworkVertex, NetworkEdge>(NetworkEdge.class);
        // add Company HQ
        NetworkVertex hqVertex = new NetworkVertex(company); 
        graph.addVertex(hqVertex);
        
        // create vertex set for subgraph
        List<NetworkVertex> tokenVertexes = getCompanyBaseTokenVertexes(company);
        Set<NetworkVertex> vertexes = new HashSet<NetworkVertex>();
        
        for (NetworkVertex vertex:tokenVertexes){
            // allow to leave tokenVertices even if those are sinks
            boolean storeSink = vertex.isSink(); vertex.setSink(false);
            vertexes.add(vertex);
            // add connection to graph
            graph.addVertex(vertex);
            graph.addEdge(vertex, hqVertex, new NetworkEdge(vertex, hqVertex, false));
            NetworkIterator_1880 iterator = new NetworkIterator_1880(mapGraph, vertex, company);
            for (;iterator.hasNext();)
                vertexes.add(iterator.next());
            // restore sink property
            vertex.setSink(storeSink);
        }

        Subgraph<NetworkVertex, NetworkEdge, SimpleGraph<NetworkVertex, NetworkEdge>> subGraph = 
            new Subgraph<NetworkVertex, NetworkEdge, SimpleGraph<NetworkVertex, NetworkEdge>>
            (mapGraph, vertexes);
        // now add all vertexes and edges to the graph
        Graphs.addGraph(graph, subGraph);

        // if addHQ is not set remove HQ vertex
        if (!addHQ) graph.removeVertex(hqVertex);
        
        // deactivate sinks on mapgraph
        NetworkVertex.initAllRailsVertices(mapGraph, null, null);
        
        // store and return
        return graph;
    }
        
    private List<NetworkVertex> getCompanyBaseTokenVertexes(PublicCompanyI company) {
        List<NetworkVertex> vertexes = new ArrayList<NetworkVertex>();
        for (TokenI token:company.getTokens()){
            NetworkVertex vertex = graphBuilder.getVertex(token);
            if (vertex == null) continue;
            vertexes.add(vertex);
        }
        return vertexes;
    }

    
}