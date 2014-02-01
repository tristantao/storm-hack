package storm.starter;
import jyinterface.factory.JythonFactory;
import jyinterface.interfaces.ClassifierType;

import org.python.util.PythonInterpreter;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import storm.starter.spout.TwitterSampleSpout;

import java.util.Map;

import org.python.util.PythonInterpreter;

/**
 * This is a basic example of a Storm topology.
 */
public class TweetTopology {

  public static class StdoutBolt extends BaseRichBolt {
    OutputCollector _collector;
    ClassifierType cT;
    
    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
      _collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
      JythonFactory jf = JythonFactory.getInstance();
      ClassifierType classifier = (ClassifierType) jf.getJythonObject(
                             "jyinterface.interfaces.ClassifierType", "classify.py");
      String result = classifier.identify((String) tuple.getValue(0));
      _collector.emit(tuple, new Values(result));
      _collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("word"));
    }
  }

  public static void main(String[] args) throws Exception {
    TopologyBuilder builder = new TopologyBuilder();

    builder.setSpout("word", 
           new TwitterSampleSpout("sjBfQvFe9kx9Vk8lWQsvg",
                 "sDsUYWuwICKIs8LZ77jGZQ1fvqFBA6Fg0ETSQyEvZU",
                 "1730288448-RXH8GlJFE8hdVbHv6PFsNWej4bTkRjYKK0skER2", 
                 "8lZiD5yAOwAEVE9IDZGA8rc3LBf9cJbXkhBAmEb2rUtgL"), 1);

    builder.setBolt("stdout", new StdoutBolt(), 3).shuffleGrouping("word");

    Config conf = new Config();
    conf.setDebug(true);

    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);

      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
    }
    else {

      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("test", conf, builder.createTopology());
      Utils.sleep(10000);
      cluster.killTopology("test");
      cluster.shutdown();
    }
  }
}
