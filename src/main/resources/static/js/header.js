const siteHeader = document.querySelector('.site-header')

if(siteHeader && siteHeader.classList.contains('site-header--hero')) {

    const headerRevealThreshold = () => window.innerHeight * 0.9

    window.addEventListener('scroll', () => {
        if (window.scrollY > headerRevealThreshold()) {
            siteHeader.classList.add('visible')
        } else {
            siteHeader.classList.remove('visible')
        }
    })
}

const userMenu = document.querySelector('.site-header__user')
const userMenuTrigger = document.querySelector('.site-header__user-trigger')

if(userMenu && userMenuTrigger) {
    userMenuTrigger.addEventListener('click', (e) => {
        e.stopPropagation()
        userMenu.classList.toggle('is-open')
    })

    document.addEventListener('click', (e) => {
        if (!userMenu.contains(e.target)) {
            userMenu.classList.remove('is-open')
        }
    })

    document.addEventListener('keydown', (e) => {
        if(e.key === 'Escape') {
            userMenu.classList.remove('is-open')
        }
    })
}