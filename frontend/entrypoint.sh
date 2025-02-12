#!/bin/sh

echo "Generating environment config..."

cat <<EOF > /app/src/assets/env.js
window.__env = {
  API_URL: "${API_URL}"
};
EOF

echo "Starting Angular..."
exec ng serve --host 0.0.0.0
