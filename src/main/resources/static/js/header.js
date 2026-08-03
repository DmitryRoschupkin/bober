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