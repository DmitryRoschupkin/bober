document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('imageModal')
    const modalImg = document.getElementById('modalImage')
    const closeBtn = document.querySelector('.image-modal__close')
    const avatarImg = document.querySelector('.js-pic-preview')

    if(avatarImg) {
        avatarImg.addEventListener('click', function () {
            modal.style.display = 'block'
            modalImg.src = this.src
        });
    }

    function closeModal() {
        modal.style.display = 'none'
    }

    if(closeBtn) {
        closeBtn.addEventListener('click', closeModal)
    }

    if(modal) {
        modal.addEventListener('click', function (event) {
            if (event.target === modal) {
                closeModal()
            }
        })
    }

    document.addEventListener('keydown', function (event) {
        if (event.key === 'Escape' && modal.style.display === 'block') {
            closeModal()
        }
    })
})