const siteHeader = document.querySelector('.site-header')
const headerRevealThreshold = () => window.innerHeight * 0.9

window.addEventListener('scroll', e => {
    document.body.style.cssText += `--scrollTop: ${this.scrollY}px`
    if (this.scrollY > headerRevealThreshold()) {
        siteHeader.classList.add('visible')
    } else {
        siteHeader.classList.remove('visible')
    }
})

gsap.registerPlugin(ScrollTrigger, ScrollSmoother)
ScrollSmoother.create({
    wrapper: `.wrapper`,
    content: `.content`
})