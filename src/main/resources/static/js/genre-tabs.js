const genreTabs = document.querySelectorAll('.genre-tab')
const bookCards = document.querySelectorAll('.book-card')

genreTabs.forEach(tab => {
    tab.addEventListener('click', () => {
        const selectedGenre = tab.dataset.filter

        genreTabs.forEach(t => t.classList.remove('is-active'))
        tab.classList.add('is-active')

        bookCards.forEach(card => {
            const matches = selectedGenre === 'all' || card.dataset.genre === selectedGenre
            card.classList.toggle('is-hidden', !matches)
        })
    })
})