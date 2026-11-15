from resvg_py import svg_to_bytes

svg_code = '<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100"><circle cx="50" cy="50" r="40" fill="red"/></svg>'
png_bytes = svg_to_bytes(svg_code)
print(f"resvg ok, png bytes: {len(png_bytes)}")
