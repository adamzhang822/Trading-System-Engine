import json
import random 

stocks = ["ORCL", "MSFT","IBM"]

for i in range(999):
    stock_num = random.randint(0,2)
    ticker = stocks[stock_num]
    buy_price = random.uniform(0.1, 1000)
    buy_quantity = random.randint(1, 1000)
    sell_price = random.uniform(0.1, buy_price)
    sell_quantity = random.randint(1, 1000)
    tick = {}
    tick["ticker"] = ticker
    tick["buy_price"] = buy_price
    tick["buy_quantity"] = buy_quantity 
    tick["sell_price"] = sell_price 
    tick["sell_quantity"] = sell_quantity
    json_object = json.dumps(tick, indent = 4)
    with open("json_file_{}.json".format(i), "w") as outfile:
        outfile.write(json_object)