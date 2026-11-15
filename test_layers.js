const fs = require('fs');
const catalog = JSON.parse(fs.readFileSync('app/src/main/assets/catalog.json', 'utf8'));

const selected = {};
const categories = ["body", "eyes", "mouth", "blush", "hair", "top", "bottom", "shoes"];
for (const cat of categories) {
    const item = catalog.items.find(i => i.category === cat);
    if (item) {
        selected[cat] = item.id;
        console.log(`Found default for ${cat}: ${item.id}, file=${item.file}, layers=${JSON.stringify(item.layers)}`);
    } else {
        console.log(`NO default found for ${cat}`);
    }
}

console.log("Selected items:", selected);

// Now resolve layers
const itemsById = {};
for (const item of catalog.items) {
    itemsById[item.id] = item;
}

const layers = [];
for (const [cat, itemId] of Object.entries(selected)) {
    const item = itemsById[itemId];
    if (item) {
        const itemLayers = item.layers || (item.file ? [{ slot: item.category, file: item.file, zOrder: item.zOrder }] : []);
        for (const l of itemLayers) {
            layers.push(l);
        }
    }
}
layers.sort((a, b) => a.zOrder - b.zOrder);
console.log("Resolved layers count:", layers.length);
console.log("Resolved layers:", layers);
