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
        System.out.println(pmap);
        generateMap(sc, map, pmap);
        
        //System.out.println(pmap);
        //System.out.println(map);
        
        //at this point the map is assembled we can start doing our
        //stuff
        
        //String start = getStart(map);
        //String start = "Blue_Mountains";
        //at this point our map is setup and our start point is created WOO
        
        
        
        Colony c = new Colony(.4, .65, .1, 100, map.get("Blue_Mountains"), map.get("Iron_Hills"), 10, map, pmap);
        c = generateColony(map, pmap);
        c.run();


    }
    public static Colony generateColony(Map map, Map pMap){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter an Alpha : " );
        double alpha = sc.nextDouble();
        System.out.print("Enter an Beta : " );
        double beta = sc.nextDouble();
        System.out.print("Enter an Rho : " );
        double rho = sc.nextDouble();
        System.out.print("Enter an Q : " );
        double qVal = sc.nextDouble();
        //Colony(double alpha, double beta, double rho, double qVal, Node start, Node end, int numOfAnts, Map map, Map pMap)
        return new Colony(alpha, beta, rho, qVal, (Node)map.get("Blue_Mountains"), (Node)map.get("Iron_Hills"), 10, map, pMap);
    }
    
    
    public static void generateMap(Scanner sc, Map<String, Node> map, Map<String, Pheromone>  pmap){
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
                Pheromone pp = new Pheromone(key, 1.0);
                if(!pmap.containsKey(key)){
                    pmap.put(key, pp);
                    pp = new Pheromone(key, 0.0);
                } else {
                    pp = (Pheromone) pmap.get(key);
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
    Map<String, Node> map;
    Map<String, Pheromone> pmap;
    File file;
    PrintStream ps;
    
    public Colony(double a, double b, double r, double q, Node s, Node e, int numOf, Map<String, Node> m, Map<String, Pheromone> p){
        //set all the fields to their arranged values
        this.start = s;
        this.end = e;
        this.alpha = a;
        this.beta = b;
        this.rho = r;
        this.qVal = q;
        this.map = m; 
        this.pmap = p;
        //make the array of ants
        ants = new ArrayList<>();
        for(int i = 0; i < numOf; i++){
            ants.add(new Ant(start, map));
        }
        try{
            file = new File("Data.txt");
            ps = new PrintStream(file);
        } catch(FileNotFoundException err){
            System.exit(1);
        }
    }
    
    public void run(){
        int count = 0;
        //System.out.println("Running");
        while(count != 25){
            //System.out.println(count);
            //for each ant in the ants array make them find end
            Iterator anty = ants.iterator();
            while(anty.hasNext()){
                Ant a = (Ant) anty.next();
                a.findEnd(end, alpha, beta);
                
            }
            
            //each ant is now in a state ready for pheromone placement
            //place new pheromones according to placement function
            for(Ant a : ants){
                //calculate how much pheromone to add the each path
                double put = qVal/a.pathLength;
                //go through each edge the and has visited and put
                //more pheromones
                for(Edge e : a.path){
                    e.updatePheromones(put, rho);
                }   
            }
            
            /*
            anty = ants.iterator();
            while(anty.hasNext()){
                Ant a = (Ant) anty.next();
                double put = qVal/a.pathLength;
                for(Edge e : a.path){
                    e.updatePheromones(put, rho);
                    
                }   
            }
            */
            
            Iterator it = (pmap).entrySet().iterator();
            while(it.hasNext()){
                Map.Entry pair = (Map.Entry)it.next();
                Pheromone temp = (Pheromone) pair.getValue();
                temp.evaporate(rho);
            }
            //System.out.println(pmap);
            
            //go through each ant and get 
            for(Ant a : ants){
                //System.out.println(a.path);
                
            }
            //reset all ants
            for(Ant a : ants){
                a.reset(start);
            }
            
            count++;
            //create an ant to run through the best pheronome path
            Ant bestRunner = new Ant(start, map);
            bestRunner.traverseBest(end);
            //System.out.println("\n\n\n\n\n\n\n\n\n");
            //System.out.println(bestRunner.path);
            System.out.print("Path Length    |    " + bestRunner.pathLength);
            System.out.println(bestRunner.path);
            ps.println(bestRunner.pathLength);
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
        map = m;
        this.current = start;
        visited = new ArrayList<>();
        path = new ArrayList<>();
        visited.add(start);
        pathLength = 0;
    }
    
    //traverse best goes from the start to the end ONLY following the highest pheromone path until
    //it reaches the end
    public void traverseBest(Node end){
        while(!current.equals(end)){
            //find the edge to transition
            Edge et = transitionBest();
            
            //check if we are returning a null edge
            if(et == null){
                System.out.println("ERROR: NULL EDGE");
                System.exit(1);
            }
                
            //find the node the edge leads too
            Node nt = map.get(et.to);

            //assign current to the node
            current = nt;
            
            //put the node into our visited node
            visited.add(current);
            
            //put the edge into our path taken
            path.add(et);
            
            pathLength += et.distance;
        }
    }
    
    private Edge transitionBest(){
        int n = 1;
        while(true){
            //take the list of edges from current
            Edge[] edges = current.edges;

            //setup an arraylist to dump cities not visited yet
            ArrayList<Edge> valid = new ArrayList<>();

            for(int i = 0; i < edges.length; i++){
                //check if the edge at position i has been visited
                if(!visited.contains(map.get(edges[i].to))){
                    //if not put it in our valid list
                    valid.add( (edges[i]));
                }
            }

            //check if the valid edges are empty
            if(valid.isEmpty()){
                current = visited.get(visited.size() - n++);
                //remove the last edge
                if(!path.isEmpty()){
                    Edge tmp = path.remove(path.size()-1);
                    this.pathLength -= tmp.distance;
                }
                continue;
            }
            Edge best = null;
            for(Edge e : valid){
                if(best == null){
                    best = e;
                } else {
                    //compare pheromones from best to e
                    if(e.getPheromones() > best.getPheromones()){
                        best = e;
                    }
                }

            }


            //return the edge
            return best;
        }
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
            Node nt = map.get(et.to);

            //assign current to the node
            current = nt;
            
            //put the node into our visited node
            visited.add(current);
            
            //put the edge into our path taken
            path.add(et);
            
            pathLength += et.distance;
        }
    }
    
    //Once all the ants move we have to reset them to the start    
    public void reset(Node start){
        visited = new ArrayList<>();
        path = new ArrayList<>();
        pathLength = 0;
        this.current = start;
        visited.add(start);
        
    }
    
    
    //transition will take an alpha and a beta and determin what edge to take
    //based on the ACO transition function
    private Edge transition(double alpha, double beta){
        int n = 1;
        while(true){
            //take the list of edges from current
            Edge[] edges = current.edges;
            
            Edge result = null;
            //setup an arraylist to dump cities not visited yet
            ArrayList<Edge> valid = new ArrayList<>();
            
            for(int i = 0; i < edges.length; i++){
                //check if the edge at position i has been visited
                if(!visited.contains(map.get(edges[i].to))){
                    //if not put it in our valid list
                    valid.add( (edges[i]));
                }
            }
            
            //System.out.print(valid);
            
            //check if the valid edges are empty
            if(valid.isEmpty()){
                current = visited.get(visited.size() - n++);
                //remove the last edge
                if(!path.isEmpty()){
                    Edge tmp = path.remove(path.size()-1);
                    this.pathLength -= tmp.distance;
                }
                continue;
            }
            
            //we now have a list of edges to pick from
            //setup the denominator of the transition function (this is the sum)
            double denom = 0.0;
            for(Edge e : valid){
                denom += getPheromones(e, alpha) + getDistance(e, beta);
            }
            //System.out.print(denom);
            
            //genearte a random double beteween 0 and denom
            Random r = new Random();
            double rand = r.nextDouble() * denom;
            //System.out.print(" " + rand + " ");
            double sum = 0.0;
            //determine the probablity of moving to each nodes using the transition
            //function
            Boolean found = false;
            Iterator i = valid.iterator();
            while(!found){

                Edge e = (Edge) i.next();
                //System.out.println(e);
                sum += getPheromones(e, alpha) + getDistance(e, beta);
                //System.out.print( " |" + sum + "| ");
                if( sum >= rand){
                        //assign our result and break from the loop
                        result = e;
                        found = true;
                }
            }
            //System.out.println(result);
            
            //return the edge
            return result;
        }
    }
    
    //this function gets tau(a, b) which is the pheromones on the edge
    //raised to alpha
    private double getPheromones(Edge e, double a){
        double p = e.getPheromones();
        return Math.pow(p,a);
    }
    
    
    //this function gets h(a, b) which is the distance portion and
    //raises it to beta
    private double getDistance(Edge e, double b){
        double d = e.getDistance();
        return Math.pow(d,b);
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
    
    public double getPheromones(){
        return pheromones.value;
    }
    
    public void updatePheromones(double d, double rho){
        pheromones.update(d*rho);
    }
    
    public void evaporatePheromones(double rho){
        pheromones.update(rho);
    }
    
    public double getDistance(){
        return distance;
    }
}

class Pheromone{
    String name;
    double value;
    
    public Pheromone(String s, double d){
        this.name = s;
        this.value = d;
    }
    
    public void update(double d){
        value = value + d;
    }
    
    public void evaporate(double rho){
        value = value * rho;
    }
    
    public String toString(){
        return name + value;
    }
    
}