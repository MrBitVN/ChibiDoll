const fs = require('fs');
const catalog = JSON.parse(fs.readFileSync('app/src/main/assets/catalog.json', 'utf8'));

console.log('Categories count:', catalog.categories.length);
const categoryCounts = {};
for (const c of catalog.categories) {
    categoryCounts[c.id] = 0;
}

let missingFiles = [];
for (const item of catalog.items) {
    categoryCounts[item.category] = (categoryCounts[item.category] || 0) + 1;
    const layers = item.layers || (item.file ? [{ file: item.file }] : []);
    for (const l of layers) {
        const filePath = 'app/src/main/assets/' + l.file;
        if (!fs.existsSync(filePath)) {
            missingFiles.push({ itemId: item.id, category: item.category, missingPath: l.file });
        }
    }
}

console.log('Item count per category:', categoryCounts);
console.log('Total items:', catalog.items.length);
console.log('Missing files count:', missingFiles.length);
if (missingFiles.length > 0) {
    console.log('Missing items details:', JSON.stringify(missingFiles, null, 2));
}
