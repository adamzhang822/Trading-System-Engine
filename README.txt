------------------------ ********************* ------------------------
Project Overview and Pattern Explanation:

This project models a trading system taking in high frequency tick data to compute analytics, flash market updates, generate trading recommendations, and then relaying to investors for final trading execution. 

The project consists of five parts, each of which is encapsulated as its own Java sub-project:

1. Data Ingestion System

Distinct EIP used:
- Polling Consumer 
- Content Based Router 
- Point-to-Point Channel 
- Mesasge Dispatcher
- Message Translator 
- Canonical Data Model 
- Invalid Message Channel
- Wiretap 
- Publish-Subscribe Channel 

This system simply has a [POLLING CONSUMER] to poll new tick data dropped into the local file system. The tick data may be in csv format or in JSON format. The files are sent as [MESSAGES] into a [CONTENT-BASED ROUTER] which routes them to CSV-specific and JSON-specific [POINT-TO-POINT CHANNEL]. The file-format specific channels then pass the files into a [MESSAGE TRANSLATOR] and convert both of them into the [CANONICAL DATA MODEL]. Any messages not that's neither in CSV format or in JSON format are passed into [INVALID MESSAGE CHANNEL] and all files polled are put into audit through [WIRETAP]. 

When passing data to be processed by [MESSAGE TRANSLATOR], the system uses a [MESSAGE DISPATCHER] as [LOAD BALANCER] between 3 processors that offer the same service. While this is not necessary for the scale of this project, this is to simulate real high frequency trading scenarios where updates actually come in each millisecond and each individual processor may not be able to handle message translation at the rate in which messages are coming in. 

For processing of JSON files, I used Java's Jackson library to map JSON into POJOs (See the Tick class under data-ingestor project) and then convert POJO to [CANONICAL DATA MODEL].

After messages have been translated into [CANONICAL DATA MODEL], they then go through another [CONTENT-BASED ROUTER] routing translated messages based on the ticker symbol of the message. These routed messages are published onto stock-specific [PUBLISH-SUBSCRIBE CHANNEL] to be consumed by relevant applications.[PUBLISH-SUBSCRIBE CHANNEL] is used because multiple applications will be using these data.

There are two applications that use these raw tick data published here: a market update flashing application that takes in these data and simply prints them out to GUI, and another analytics engine that ingests these data to produce statistics to be used for trading. Both are explained below.

2. Market Flasher System 
Distinct EIP used: 
- Publish-Subscribe Channel 
- Point-to-Point Channel 
- Messaging Gateway
- Aggregator (for Throttling) 

This is a separate application from the rest of this project, and emulates a simple GUI system that just prints raw market data onto some GUI. 

The reason for writing up this system is because some traders may not want to delegate all of their trading decisions to automated algorithms. They may want to look at charts and make decisions manually. 

We would need a system to take in data points and render them on charts.

Here, the system takes in raw tick data published and simply stores them in a file (assuming these files will then be used by other GUI applications to create charts).

Because updates may be coming in millisecond basis, the GUI may not be able to handle it. Because stock price fluctuations from millisecond to millisecond does not really show significant differences on charts, we can safely ignore some of the messages to lessen the burden on the system.

Therefore, here I use the [AGGREGATOR] pattern to aggregate 3 consecutive messages (simply overwriting previous message with the newest one) to throttle the rate at which updates arrive at the system. If there are less than 3 messages arriving within 5 milliseconds, then only the messages that has arrived within the previous 5 milliseconds will be aggregated. 

The throttled messages will then be sent to UpdateFlasher class through a [MESSAGING GATEWAY] and processed by it to be printed out to a file.

3. Analytics Engine  
Distinct EIP Used:
- Publish-Subscribe Channel 
- Messaging Gateway 

Distinct GoF Patterns Used:
- Singleton
- Template Methods

Another application that uses raw tick data published by Data Ingestion System is the Analytics Engine.

We assume that Analytics Engine has high processing power and want to take advantage of millisecond level updates to gain an edge in its trading strategies. Therefore, we don't need to throttle the raw data coming into the system. 

After reading messages published onto publish-subscribe channels, the Analytics Engine applies computes different disperion statistics (Standard Deviation, Average Absolute Deviation, Median Absolute Deviation, Maximum Absolute Deviation) for the buy price of each stock. 

Because these computations all share few steps (computing the mean of buy prices, then compute sum of deviations in some way, and then normalize the sum in some way), [TEMPLATE METHOD] pattern is used to implement these computations.

After the statistics are computed for each stocks, the data are published to [PUBLISH-SUBSCRIBE CHANNELS] for each combination of stock and statistics. In a larger system, this approach can result in prohibitively large number of distinct channels. However, here we assume there is a reasonable upper limit to the stocks and statistics that can be added. 

In such case, having distinct channel for each stock+statistics combination will make it easier for new Robo Advisor (explained below) to attach to the system and get working on their distinct stock + stat combination strategies.

4. Robo Advisor 
Distinct EIP Used:
- Publish-Subscribe Channel
- Message Gateway 

Distinct GoF Patterns Used:
- Strategy 

Each Robo Advisor has list of stocks they need to look out for, and they are equipped with a strategy to assess trading strategies on the stocks.

The trading strategies determine which statistics Robo Advisors need to get for each of its stocks, and how to derive Sell, Buy, Hold recommendation based on these statistics. 

I employed the [STRATEGY] pattern in which there are 3 strategies (BuyVolatile, NoVolatility, MiddleOfTheRoad). Each strategies return a list of statistics names (STDEV, MAD...etc) needed for it to compute recommendations, and an algorithm to compute recommendations. These trading algorithms are not based on any real algorithms and are just mostly arbitrary rules using artbirary statistics. They are implemented as proof of concept for this type of system architecture.

Robo Advisors are equipped with a strategy to derive trading recommendations based on data dequed from jms topics. Each Robo Advisors then publish their own recommendations to their own [PUBLISH-SUBSCRIBE CHANNELS] for individual investment accounts to process.


5. Investment Accounts 
Distinct EIP used: 
- Publish-Subscribe Channel 
- Message Gateway 
- Aggregator 
- Blackboard 

Distinct GoF Patterns Used:
- State 

Each Investment Account represents a Investor. They are subscribed to a list of Robo Advisors and they are each under a certain state of mind which influences the way they process recommendations from Robo Advisors. 

First, the Investment Accounts will collect recommendation data from jms:topics that they are subscribed to. then an [AGGREGATOR] aggregates the reommendations and tallies them to derive at a final trading recommendation in a pattern similar to [BLACKBOARD]. 

Finally, the InvestmentAccount makes final trading decision by looking at the recommendations and processing them under a certain state of mind (PanicSelling, DiamondHands, which means never selling antyhing, and Neutral, which just takes the recommendations as they are). This part is implemented using [STATE] pattern. After processing each rounds of recommendations and making a trading decision, the InvestmentAccounts switch to another state randomly, representing the random sentiments of players in the market.

------------------------ ********************* ------------------------

Set Up Instructions:
Using Eclipse version 2022-03, please import each project as existing Maven projects.

Make sure ActiveMQ version 5.15.12 is in your system. 

------------------------ ********************* ------------------------
Run Instructions:
Due to the concurrent nature of the system, please execute the files in the following orders to get the expected behavior of the system.

Step1: 
Make sure ActiveMQ is running on your system

Step2: Data Producer:

Run Producer.java under data-ingestor project, wait till all files are processed by making sure that all files in data/inbox are moved to data/outbox. Check ActiveMQ that the files have been enqueed to respective jms queues as messages. There are total of 999 JSON files, 999 CSV files, and 1 python.py file, which will be put into the invalid message channel.


Step3: Start subscriptions, try flashing market updates and start analytics engine:

Before publishing any processed messages to jms topics, first start the subscribes. 
- Start Subscriber.java under analytics-engine's edu.uchicago.adamzhang22.analytics_main package.
- Also start Flasher.java under market-flasher's edu.uchicago.adamzhang22.market_flasher package.
- Start putting processed data onto jms topics by starting DataTranslator.java under data-ingestor's edu.uchicago.adamzhang22.data_ingestor package
- After some time, start and wait for execution of ClientApp.java under market-flasher's edu.uchicago.adamzhang22.market_flasher package to print out update data to a txt file. 
- To check everything has been ran properly, first check that there is an updates.txt created under the project folder for market-flasher containing throttled market tick data. 
- Then check that messages have been enqueued on jms:queue:ticker_analytics in ActiveMQ. The analytics engine will dequeue these messages to produce statistics 

Step4: Produce analytics, let robo advisors and investors do the processing and check outputs:
Before publishing analytics to jms topics, first start the subscribers
- Start InvestorManager.java under investment-accounts project's edu.uchicago.adamzhang22.accounts package 
- Start RoboManager.java under robo-advisors proejct's edu.uchicago.adamzhang22.robo package 
- Start AnalyticsProducer.java under analytics-engine's edu.uchicago.adamzhang22.analytics_main package 
- Check console outputs from InvestorManager.java for final trading decisions


