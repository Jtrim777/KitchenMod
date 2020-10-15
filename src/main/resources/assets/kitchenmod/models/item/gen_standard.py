template = """
{
  "parent": "item/generated",
  "textures": {
    "layer0": "kitchenmod:item/ITEM_NAME"
  }
}
"""

make = []
while True:
    item = input("Item name: ")
    if item == "*": break
    make.append(item)

for item in make:
    with open("%s.json" % item, 'w') as ofile:
        ofile.write(template.replace("ITEM_NAME", item))
