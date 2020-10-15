import json

body = {"parent":"kitchenmod:block/stake_crop", "textures": {"plant":""}}

name = input("Plant name: ")
count = int(input("Max age: "))

for i in range(count+1):
  body["textures"]["plant"] = "kitchenmod:block/%s%i" % (name, i)

  with open("%s%i.json" % (name, i), 'w') as ofile:
    json.dump(body, ofile)