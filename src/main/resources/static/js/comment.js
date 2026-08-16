document.addEventListener('click', (e) => {
    const replyBtn = e.target.closest('.comment__reply-btn')
    if(!replyBtn) return

    const commentBody = replyBtn.closest('.comment__body')
    const form = commentBody.querySelector(':scope > .comment__reply-form')
    if (!form) return

    form.classList.toggle('is-open')

    if(form.classList.contains('is-open')) {
        form.querySelector('.comment__reply-input')?.focus()
    }
})