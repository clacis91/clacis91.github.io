const ghpages = require('gh-pages')

// replace with your repo url
ghpages.publish(
  'public',
  {
    branch: 'master',
    repo: 'https://github.com/clacis91/clacis91.github.io',
  },
  () => {
    console.log('Deploy Complete!')
  }
)
