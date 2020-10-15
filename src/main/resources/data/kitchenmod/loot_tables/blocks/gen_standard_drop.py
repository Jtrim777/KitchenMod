block_name = input("Block name: ")

out = """
{
  "type" : "minecraft:block",
  "pools" : [
    {
      "rolls" : 1,
      "entries" : [
        {
          "type" : "item",
          "name" : "kitchenmod:%s"
        }
      ]
    }
  ]
}""" % block_name

with open("%s.json" % block_name, 'w') as oFile:
  oFile.write(out)