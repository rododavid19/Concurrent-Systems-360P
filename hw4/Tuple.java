package hw4;


import org.apache.hadoop.mapred.join.TupleWritable;

public class Tuple<X, Y> extends TupleWritable {
    public final X x;
    public final Y y;


    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}

//public class Tuple<X, Y> {
//    public final X x;
//    public final Y y;
//    public Tuple(X x, Y y) {
//        this.x = x;
//        this.y = y;
//    }
//}