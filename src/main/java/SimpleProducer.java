//import util.properties packages
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

//import simple producer packages
//import KafkaProducer packages
//import ProducerRecord packages

//Create java class named “SimpleProducer”
public class SimpleProducer {

    public static void main(String[] args) throws Exception{

        // Check arguments length value
//        if(args.length == 0){
//            System.out.println("Enter topic name");
//            return;
//        }
        //Assign topicName to string variable
        String topicName = "topic-1";

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        System.out.println("Finding Server...");
        props.put("bootstrap.servers", "kafka-broker-1:9092");
        System.out.println("Server Found");

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

                //If the request fails, the producer can automatically retry,
                props.put("retries", 0);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<String, String>(props);

        for(int i = 0; i < 10; i++)
            producer.send(new ProducerRecord<String, String>(topicName,
                    Integer.toString(i), Integer.toString(i))).get();
        System.out.println("Message sent successfully");
        producer.close();

        callConsumer(topicName);
    }

    public static void callConsumer(String topic){
        SimpleConsumer simpleConsumer = new SimpleConsumer();
        final Consumer<Long, String> consumer = simpleConsumer.createConsumer(topic);

        final int giveUp = 100;   int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<Long, String> consumerRecords =
                    consumer.poll(1000);

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%s, %s, %s, %s)\n",
                        record.key(), record.value(),
                        record.partition(), record.offset());
            });

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
    }
}
