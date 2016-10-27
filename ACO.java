import java.io.*;
import java.util.*;

public class ACO{
    public static void main(String[] args) throws FileNotFoundException{
        //yes my data file is hard coded
        File f = new File("DataExt.dat");
        
        //setup a scanner on that file
        Scanner sc = new Scanner(f);
        
        //create my map
        Map<String, Node> map =  new TreeMap<>();
        Map<String, Pheromone> pmap = new TreeMap<>();
        
        generateMap(sc, map, pmap);
        System.out.println(pmap);
        //System.out.println(map);
        
        //at this point the map is assembled we can start doing our
        //stuff
        
        String start = getStart(map);
        //String start = "Blue_Mountains";
        //at this point our map is setup and our start point is created WOO
        

        
    }
    
    
    public static void generateMap(Scanner sc, Map map, Map pmap){
        //go through each line in the file
        while(sc.hasNextLine()){
            String name = sc.nextLine();

            int distance = Integer.parseInt(sc.nextLine());

            int n = Integer.parseInt(sc.nextLine());

            Edge[] edges = new Edge[n];
            for(int i = 0; i < n; i++){
                String edge = sc.nextLine();

                Scanner ss = new Scanner(edge);
                //get the name
                String to = ss.next();
                
                //get the distance to the node
                int dis = Integer.parseInt(ss.next());
                
                //get the road condition
                int rc = Integer.parseInt(ss.next());
                
                //get the danger level
                double dan = Double.parseDouble(ss.next());
                
                String key = ss.next();
                Pheromone pp = null;
                if(!pmap.containsKey(key)){
                    pmap.put(key, pp);
                    pp = new Pheromone(key, 0.0);
                } else {
                    pp = pmap.get(key);
                }
                
                //put the edge into the array
                edges[i] = new Edge(to, dis, rc, dan, pp);
                
            }

            Node nod = new Node(name, distance, edges);
            map.put(name, nod);
            //System.out.println(name + " " + nod);
            sc.nextLine();
        }
    }
    
    public static String getStart(Map map){
        String start = "";
        boolean s = true;
        Scanner ls = new Scanner(System.in);
        while(s){
            System.out.println("Please pick a city to start from:");
            for(Object name : map.keySet()){
                String str = (String) name;
                System.out.println(str.replaceAll("_", " "));
            }
            start = ls.nextLine();
            System.out.println(start);
            if(map.containsKey(start.replaceAll(" ", "_"))){
                s = !s;
                System.out.println("City: " + start);
                start = start.replaceAll(" ", "_");
            } else {
                System.out.println("INVALID CITY press enter");
                ls.nextLine();
            } 
            
        }
        return start;
    }
}


class Colony{
    ArrayList<Ant> ants;  //array of Ants
    Node start;           //node all ants start at
    Node end;             //node all ants end at
    double alpha;         //alpha value for transition
    double beta;          //beta value for transition
    double rho;           //used for placement & evaporation
    double qVal;          //amount of pheromones for the ant
    Map map;
    
    public Colony(double a, double b, double r, double q, Node s, Node e, int numOf, Map m){
        //set all the fields to their arranged values
        this.start = s;
        this.end = e;
        this.alpha = a;
        this.beta = b;
        this.rho = r;
        this.qVal = q;
        this.map = map;
        
        //make the array of ants
        ants = new ArrayList<>();
    }
    
    public void run(){
        int count = 0;
        while(count != 25){
            //for each ant in the ants array make them find end
            for(Ant a : ants.iterator()){
                a.findEnd(end);
            }
            
            //each ant is now in a state ready for pheromone placement
            
            //evaporate pheromones first
            
            //place new pheromones according to placement function
            
            //reset all ants
            for(Ant a : ants.iterator()){
                a.reset(start);
            }
            
            count++;
            
        }
    }
    
}

class Ant{
    ArrayList<Node> visited;
    ArrayList<Edge> path;
    Node current;
    int pathLength;
    Map<String, Node> map;
    
    public Ant(Node start, Map<String, Node> m){
        this.current = start;
        visisted = new ArrayList<>();
        path = new ArrayList<>();
        visited.add(curr);
        pathLength = 0;
    }
    
    //The find end function will take an end node and go from the start to the
    //end by using the transition function. The transition function finds the
    //next node to traverse to, continue until the ant moves into the end node.
    public void findEnd(Node end, double alpha, double beta){
        //loop through until the node returned by transition is the end
        while(!current.equals(end)){
            //find the edge to transition
            Edge et = transition(alpha, beta);
            
            //check if we are returning a null edge
            if(et == null){
                System.out.println("ERROR: NULL EDGE");
                System.exit(1);
            }
                
            //find the node the edge leads too
            Node nt = map.get(ed.to);

            //assign current to the node
            current = nt;
            
            //put the node into our visited node
            visited.add(current);
            
            //put the edge into our path taken
            path.add(ed);
            
            pathLength += et.distance;
        }
    }
    
    //Once all the ants move we have to reset them to the start    
    public void Reset(Node start){
        visisted = new ArrayList<>();
        path = new ArrayList<>();
        pathLength = 0;
        this.current = start;
        visited.add(start);
        
    }
    
    
    //transition will take an alpha and a beta and determin what edge to take
    //based on the ACO transition function
    private Edge transition(double alpha, double beta){
        //take the list of edges from current
        Edge[] edges = current.edges;
        
        //setup an arraylist to dump cities not visited yet
        ArrayList<Edge> valid = new ArrayList<>();
        
        for(int i = 0; i < edges.length; i++){
            //check if the edge at position i has been visited
            if(!visited.contains(map.get(edge[i].to))){
                //if not put it in our valid list
                valid.add(map.get(edge[i].to));
            }
        }
        
        //check if the valid edges are empty
        if(valid.isEmpty()){
            //double back a the current node and rerun transition
            return null;
        }
        
        //determine the probablity of moving to each nodes using the transition
        //function

        //generate a random number between 0 and the sum of the probability 
        //and pick the edge
        
        //return the edge
        return null;
    }
}

//the node object keeps track of a few things about the city
//1 it keeps track of the city name, the heuristical disance 
//to the destination and the array of edges to other cities
class Node{
    
    
    String name;    //name of the city the node represents
    int distance;   //heuristical distance (unused in aco)
    Edge[] edges;   //list of edges extending from the node
    
    
    public Node(String name, int distance, Edge[] e){
        this.name = name;
        this.distance = distance;
        this.edges = e;  
    }
    
    public String toString(){
        return name + " goes to " + Arrays.toString(edges);
    }
    
    public Edge[] getEdges(){
        return edges;
    }
    
    public boolean equals(Object other){
        if(other instanceof Node){
            return name.equals(((Node)other).name);
        } else {
            return false;
        }
    }
}

class Edge{
    String to;
    int distance;       //actual distance
    int roadcon;        //road condition (not used)
    double danger;      //danger on road (not used)
    Pheromone pheromones;  //pheromones on path 
    
    public Edge(String a, int dis, int rc, double dan, Pheromone p){
        to = a;
        distance = dis;
        roadcon = rc;
        danger = dan;
        pheromones = p;
    }
    
    public String toString(){
        return to + " " + distance + " " + roadcon + " " + danger;
    }
}

class Pheromone{
    String name;
    double value;
    
    public Pheromone(String s, double d){
        this.name = s;
        this.value = d;
    }
    
    public String toString(){
        return name + value;
    }
    
}