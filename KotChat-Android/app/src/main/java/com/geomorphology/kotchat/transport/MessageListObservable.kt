package com.geomorphology.kotchat.transport

import com.geomorphology.kotchat.model.Message
import rx.Observable
import rx.lang.kotlin.PublishSubject
import rx.subjects.PublishSubject

public object MessageListObservable {
    val mPublishSubject : PublishSubject<Message> = PublishSubject()

    public fun postMessage(message: Message) {
        mPublishSubject.onNext(message)
    }

    public fun getObservable(): Observable<Message> {
        return mPublishSubject
    }
}