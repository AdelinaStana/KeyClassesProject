package ranking;

public enum ClassRankingProperties {

    PAGERANK_DIRECTED("PR", "PR"),
    PAGERANK_UNDIRECTED1("PR_U", "PR_U"),
   // PAGERANK_UNDIRECTED2("PR-U", "PR_undir2"), no meaning if no weights
   // PAGERANK_UNDIRECTED4("PR-U", "PR_undir4"), no meaning if no weights
    PAGERANK_DIRECTED_W("PR_W", "PR_W"),
    PAGERANK_UNDIRECTED1_W("PR_U_W", "PR_U_W"),
    PAGERANK_UNDIRECTED2_W("PR_U2_W", "PR_U2_W"),
    PAGERANK_UNDIRECTED4_W("pagerank_undirected4_W", "PR_undir4_W"),
    BTW_DIRECTED_W("BETW_W", "BETW_W"),
    BTW_DIRECTED("BETW", "BETW"),
    BTW_UNDIRECTED_W("BETW_U_W", "BETW_U_W"),
    BTW_UNDIRECTED_W2("BETW_U2_W", "Betw_U2_W"),
    BTW_UNDIRECTED("BETW_U", "BETW_U"),
    
    CC_DIRECTED_W("CC_W", "CC_W"),
    CC_DIRECTED("CC", "CC"),
    CC_UNDIRECTED_W("CC_U_W", "CC_U_W"),
    CC_UNDIRECTED("CC_U", "CC_U"),
    
    HITS_AUTHORITY("AUTH", "AUTH"),
    HITS_HUB("HUB", "HUB"),
    HITS_AUTHORITY_W("AUTH_W", "AUTH_W"),
    HITS_HUB_W("HUB_W", "HUB_W"),
    FIELD_NR("NoF", "NoF"),
    METHOD_NR("NoM", "NoM"),
    PFIELD_NR("NoPF", "NoPF"),
    PMETHOD_NR("NoPM", "NoPM"),
    SIZE("SIZE", "SIZE"),
    CONN_TO_ROW("CONN_IN"),
    CONN_TO_COL("CONN_OUT"),
    CONN_TOTAL("CONN_TOTAL"),
    WEIGHT_IN("CONN_IN_W"),
    WEIGHT_OUT("CONN_OUT_W"),
    WEIGHT_CONN_TOTAL("CONN_TOTAL_W"),
    FUZZY_DECISION("fuzzy_decision", "Fuzzy_decision"), 
    LEVEL("level", "Level"),
    
    KCORE10("KCORE", "KCORE"),
    W_KCORE10("KCORE_W", "KCORE_W"),
    
    DIRECTTOP("DIRECT_TOP", "DIRECT_TOP"), 
    DIRECTTOP_IN("DIRECT_TOP_IN"), 
    DIRECTTOP_OUT("DIRECT_TOP_OUT"), 
    DIRECTTOP_W("DIRECT_TOP_W"),
	 DIRECTTOP_W_IN("DIRECT_TOP_IN_W"),
	 DIRECTTOP_W_OUT("DIRECT_TOP_OUT_W");
   
	private String name;
    private String description;

    ClassRankingProperties(String name){
        this.name = name;
    }

    ClassRankingProperties(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description == null ? name : description;
    }

    public static ClassRankingProperties[] getRankingPropertiesOrderForPrinting(){
        return new ClassRankingProperties[]{ FIELD_NR, METHOD_NR, SIZE, 
        		CONN_TO_ROW, CONN_TO_COL, CONN_TOTAL, WEIGHT_IN, WEIGHT_OUT, WEIGHT_CONN_TOTAL, 
        		PAGERANK_DIRECTED, PAGERANK_UNDIRECTED1,
        		PAGERANK_DIRECTED_W, PAGERANK_UNDIRECTED1_W,PAGERANK_UNDIRECTED2_W,PAGERANK_UNDIRECTED4_W, 
        		HITS_AUTHORITY, HITS_HUB, HITS_AUTHORITY_W, HITS_HUB_W, FUZZY_DECISION, LEVEL,
        	     KCORE10,W_KCORE10,
        	     BTW_DIRECTED_W, BTW_DIRECTED, BTW_UNDIRECTED_W, BTW_UNDIRECTED_W2, BTW_UNDIRECTED,
        	     CC_DIRECTED_W, CC_DIRECTED, CC_UNDIRECTED_W, CC_UNDIRECTED,
        	     DIRECTTOP,DIRECTTOP_IN, DIRECTTOP_OUT,DIRECTTOP_W, DIRECTTOP_W_IN,DIRECTTOP_W_OUT,};
    }
}
