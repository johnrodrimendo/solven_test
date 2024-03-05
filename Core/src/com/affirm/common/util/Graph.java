package com.affirm.common.util;


import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Graph {
    private Integer vertices;
    private boolean buildTranspose;
    private boolean showTranspose;


    private ArrayList<Integer> adjacency[];
    private ArrayList<Integer> transpose[];

    private ArrayList<Integer> sources;
    private ArrayList<Integer> targets;

    Integer graphSize;

    public Graph() {
        buildTranspose = false;
        showTranspose = false;
        this.graphSize = 0;
    }

    public void configure(Integer vertices, boolean buildTranspose) {
        this.vertices = vertices;
        this.buildTranspose = buildTranspose;
        this.init();
    }

    private void init ()  {
        adjacency = new ArrayList[vertices + 1];
        transpose = new ArrayList[vertices + 1];

        for(Integer i = 0 ; i <= vertices ; ++i) {
            adjacency[i] = new ArrayList<>();
            transpose[i] = new ArrayList<>();
        }
    }

    public void addEdge(Integer from, Integer to) {
        adjacency[from].add(to);
        if(buildTranspose) {
            transpose[to].add(from);
        }
    }

    public ArrayList<Integer>[] getAdjacency() {
        if(this.showTranspose) {
            return transpose;
        }
        return adjacency;
    }

    public void setAdjacency(ArrayList<Integer>[] adjacency) {
        this.adjacency = adjacency;
    }

    public void buildSourcesTargets(){
        sources = new ArrayList<>();
        targets = new ArrayList<>();
        for(Integer i = 0 ; i < vertices + 1 ; ++i) {
            if(adjacency[i].isEmpty()) {
                targets.add(i);
            }
        }

        for(Integer i = 0 ; i < vertices + 1 ; ++i) {
            if(transpose[i].isEmpty()) {
                sources.add(i);
            }
        }
    }

    public ArrayList<Integer> getSources() {
        return sources;
    }

    public void setSources(ArrayList<Integer> sources) {
        this.sources = sources;
    }

    public ArrayList<Integer> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<Integer> targets) {
        this.targets = targets;
    }

    public Integer getVertices() {
        return vertices;
    }

    public void setVertices(Integer vertices) {
        this.vertices = vertices;
    }

    public void invert () {
        this.showTranspose ^= true;
    }

    public static Graph merge(Graph first, Graph second) {
        Graph result = new Graph();
        result.configure(first.getVertices(), true);

        Set<Pair<Integer, Integer>> mapped = new HashSet<>();

        for(int from  = 0 ; from < first.getVertices() + 1 ; ++from) {
            ArrayList<Integer> connections = first.adjacency[from];

            for(Integer to : connections){
                Pair<Integer, Integer> edge = Pair.of(to, from);
                if(!mapped.contains(edge)) {
                    mapped.add(edge);
                    result.addEdge(from, to);
                }
            }
        }

        for(int from  = 0 ; from < second.getVertices() + 1 ; ++from) {
            ArrayList<Integer> connections = second.adjacency[from];

            for(Integer to : connections){
                Pair<Integer, Integer> edge = Pair.of(to, from);
                if(!mapped.contains(edge)) {
                    mapped.add(edge);
                    result.addEdge(from, to);
                }
            }
        }

        return result;
    }

    public Integer getGraphSize() {
        return graphSize;
    }

    public void setGraphSize(Integer graphSize) {
        this.graphSize = graphSize;
    }
}
